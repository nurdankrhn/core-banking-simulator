package CoreBankingSimulator.services;

import CoreBankingSimulator.model.Account;
import CoreBankingSimulator.model.FraudAlert;
import CoreBankingSimulator.model.Transaction;
import CoreBankingSimulator.repository.AccountRepository;
import CoreBankingSimulator.repository.FraudAlertRepository;
import CoreBankingSimulator.repository.TransactionRepository;
import CoreBankingSimulator.services.kafka.TransactionEventPublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EndOfDayService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionEventPublisher transactionEventPublisher;
    private final FraudAlertRepository fraudAlertRepository;

    // Runs daily at 23:59
    @Scheduled(cron = "0 59 23 * * ?")
    public void runEndOfDayProcessing() {
        System.out.println("[EOD] Starting end-of-day processing: " + LocalDate.now());

        calculateInterest();
        closePendingTransactions();
        processFraudBatch();
        exportDailySummary();

        System.out.println("[EOD] End-of-day processing finished.");
    }

    private void calculateInterest() {
        List<Account> accounts = accountRepository.findAll();
        for (Account account : accounts) {
            if (account.getAccountType().equalsIgnoreCase("SAVINGS")) {
                BigDecimal interestRate = new BigDecimal("0.01");
                BigDecimal interest = account.getBalance().multiply(interestRate);
                account.setBalance(account.getBalance().add(interest));
                accountRepository.save(account);

                System.out.println("[EOD] Interest added to account " + account.getIban() + ": " + interest);
            }
        }
    }

    private void closePendingTransactions() {
        List<Transaction> pending = transactionRepository.findByStatus("PENDING");
        for (Transaction tx : pending) {
            tx.setStatus("CLOSED");
            transactionRepository.save(tx);
            System.out.println("[EOD] Closed pending transaction: " + tx.getId());
        }
    }

    private void processFraudBatch() {
        List<FraudAlert> unreviewedAlerts = fraudAlertRepository.findByReviewedByAdminFalse();
        for (FraudAlert alert : unreviewedAlerts) {
            transactionEventPublisher.publishFraudAlert(
                    alert.getAccount(),
                    "Suspicious transaction detected: " + alert.getTransaction().getAmount()
            );
            alert.setReviewedByAdmin(true);
            fraudAlertRepository.save(alert);
        }
    }

    // Daily summary export
    private void exportDailySummary() {

        LocalDate today = LocalDate.now();
        OffsetDateTime start = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);

        List<Transaction> transactions = transactionRepository.findByCreatedAtBetween(start, end);



        Path filePath = Paths.get("sumExportToday.json");

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            String json = mapper.writeValueAsString(transactions);

            Files.write(
                    filePath,
                    json.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("[EOD] Daily JSON summary saved to sumExportToday.json");

        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

}



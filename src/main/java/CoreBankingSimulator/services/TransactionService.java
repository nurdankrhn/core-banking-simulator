package CoreBankingSimulator.services;

import CoreBankingSimulator.dto.NotificationEvent;
import CoreBankingSimulator.model.Account;
import CoreBankingSimulator.model.Transaction;
import CoreBankingSimulator.repository.AccountRepository;
import CoreBankingSimulator.repository.TransactionRepository;
import CoreBankingSimulator.services.kafka.NotificationEventPublisher;
import CoreBankingSimulator.services.kafka.TransactionEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionEventPublisher eventPublisher;

    @Autowired
    private NotificationEventPublisher notificationPublisher;

    private boolean isSuspicious(Account sourceAccount, BigDecimal amount) {

        //Large Amount Rule
        if (amount.compareTo(new BigDecimal("10000")) > 0) {
            return true;
        }

        //Exceeds 90% of daily limit
        BigDecimal ninetyPercent = sourceAccount.getDailyLimit().multiply(new BigDecimal("0.9"));
        if (sourceAccount.getDailyTransferredAmount().add(amount).compareTo(ninetyPercent) > 0) {
            return true;
        }

        //Minimum balance nearly violated
        BigDecimal remaining = sourceAccount.getBalance().subtract(amount);
        if (remaining.compareTo(sourceAccount.getMinBalance().add(new BigDecimal("50"))) < 0) {
            return true;
        }

        return false;
    }


    // ===================== Deposit =====================
    @Transactional
    public Transaction deposit(Long accountId, BigDecimal amount, String description, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // ===================== Update balance =====================
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        // ===================== Create transaction =====================
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("DEPOSIT");
        tx.setDirection("IN");
        tx.setDescription(description);
        tx.setStatus("POSTED"); // immediately posted for deposits
        tx.setCreatedBy(userId);
        tx.setCreatedAt(OffsetDateTime.now());
        tx.setUpdatedAt(OffsetDateTime.now());

        // ===================== Publish events =====================
        // First, transaction validated
        eventPublisher.publishValidated(tx);

        // Save transaction to DB
        tx = transactionRepository.save(tx);

        // Then, transaction posted
        eventPublisher.publishPosted(tx);

        // ======= SUCCESS NOTIFICATION =======
        notificationPublisher.publishNotification(new NotificationEvent(
                account.getCustomer().getId(),
                account.getCustomer().getEmail(),
                "SUCCESS",
                "Deposit of " + amount + "₺ completed successfully.",
                LocalDateTime.now()));

        return tx;
    }


    // ===================== Withdraw =====================
    @Transactional
    public Transaction withdraw(Long accountId, BigDecimal amount, String description, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            // ======= FAILED NOTIFICATION =======
            notificationPublisher.publishNotification(new NotificationEvent(
                    account.getCustomer().getId(),
                    account.getCustomer().getEmail(),
                    "FAILED",
                    "Withdrawal failed: insufficient balance.",
                    LocalDateTime.now()
            ));
            throw new RuntimeException("Insufficient balance");
        }

        // ===================== Update balance =====================
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        // ===================== Create transaction =====================
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setType("WITHDRAW");
        tx.setDirection("OUT");
        tx.setDescription(description);
        tx.setStatus("POSTED"); // immediately posted for withdraws
        tx.setCreatedBy(userId);
        tx.setCreatedAt(OffsetDateTime.now());
        tx.setUpdatedAt(OffsetDateTime.now());

        // ===================== Publish events =====================
        eventPublisher.publishValidated(tx); // validated before saving
        tx = transactionRepository.save(tx);  // save transaction
        eventPublisher.publishPosted(tx);    // posted after saving

        // ======= SUCCESS NOTIFICATION =======
        notificationPublisher.publishNotification(new NotificationEvent(
                account.getCustomer().getId(),
                account.getCustomer().getEmail(),
                "SUCCESS",
                "Withdrawal of " + amount + "₺ completed successfully.",
                LocalDateTime.now()
        ));

        return tx;
    }


    // ===================== Transfer =====================
    @Transactional
    public Transaction transfer(Long accountId, String targetIban, BigDecimal amount, String description, Long userId) {
        Account sourceAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account targetAccount = accountRepository.findByIban(targetIban)
                .orElseThrow(() -> new RuntimeException("Target account not found"));

        if (sourceAccount.getBalance().compareTo(amount) < 0
                || sourceAccount.getDailyLimit().compareTo(amount) < 0) {
            System.out.println("getDailyLimit: " + sourceAccount.getDailyLimit());

            // ======= FAILED NOTIFICATION =======
            notificationPublisher.publishNotification(new NotificationEvent(
                    sourceAccount.getCustomer().getId(),
                    sourceAccount.getCustomer().getEmail(),
                    "FAILED",
                    "Transfer failed: insufficient balance or daily limit exceeded.",
                    LocalDateTime.now()
            ));
            throw new RuntimeException("Insufficient balance");
        }

        boolean suspicious = isSuspicious(sourceAccount, amount);

        if (suspicious) {
            eventPublisher.publishFraudAlert(sourceAccount, "SUSPICIOUS ACTIVITY DETECTED");

            // ======= FRAUD NOTIFICATION =======
            notificationPublisher.publishNotification(new NotificationEvent(
                    sourceAccount.getCustomer().getId(),
                    sourceAccount.getCustomer().getEmail(),
                    "FRAUD",
                    "Suspicious activity detected during your transfer request.",
                    LocalDateTime.now()
            ));
        }

        // ===================== Update balances =====================
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));
        accountRepository.save(sourceAccount);
        accountRepository.save(targetAccount);

        // ===================== Create transaction =====================
        Transaction tx = new Transaction();
        tx.setAccount(sourceAccount);
        tx.setAmount(amount);
        tx.setType("TRANSFER");
        tx.setDirection("OUT");
        tx.setDescription(description + " | To: " + targetIban);
        tx.setStatus("POSTED"); // immediately posted for transfers
        tx.setCreatedBy(userId);
        tx.setCreatedAt(OffsetDateTime.now());
        tx.setUpdatedAt(OffsetDateTime.now());

        // ===================== Publish events =====================
        eventPublisher.publishValidated(tx); // validated before saving
        tx = transactionRepository.save(tx);  // save transaction
        eventPublisher.publishPosted(tx);    // posted after saving

        // ======= SUCCESS NOTIFICATION =======
        notificationPublisher.publishNotification(new NotificationEvent(
                sourceAccount.getCustomer().getId(),
                sourceAccount.getCustomer().getEmail(),
                "SUCCESS",
                "Your transfer of " + amount + "₺ to " + targetIban + " was successful.",
                LocalDateTime.now()
        ));

        return tx;
    }

    // ===================== Transaction History =====================
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private void sendTransactionCreatedEvent(Transaction tx) {
        String message = "Transaction created: ID=" + tx.getId() + ", Amount=" + tx.getAmount();
        kafkaTemplate.send("transaction.created", message);
    }


}


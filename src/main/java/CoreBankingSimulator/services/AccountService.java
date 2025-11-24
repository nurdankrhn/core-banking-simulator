package CoreBankingSimulator.services;

import CoreBankingSimulator.exceptions.InsufficientBalanceException;
import CoreBankingSimulator.exceptions.TransferLimitExceededException;
import CoreBankingSimulator.model.Account;
import CoreBankingSimulator.model.Customer;
import CoreBankingSimulator.repository.AccountRepository;
import CoreBankingSimulator.repository.CustomerRepository;
import CoreBankingSimulator.util.IbanGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final IbanGenerator ibanGenerator;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          IbanGenerator ibanGenerator) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.ibanGenerator = ibanGenerator;
    }

    @Transactional
    public Account createAccount(Long customerId, String type) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(type);
        account.setIban(ibanGenerator.generateIban(customerId));
        account.setBalance(BigDecimal.ZERO);
        account.setStatus("ACTIVE");

        account.setDailyLimit(new BigDecimal("10000"));
        account.setDailyTransferredAmount(BigDecimal.ZERO);
        account.setMinBalance(BigDecimal.ZERO);

        return accountRepository.save(account);
    }

    public List<Account> listAccountsForCustomer(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public Account getAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Transactional
    public BigDecimal getBalance(Long accountId) {
        return getAccount(accountId).getBalance();
    }

    @Transactional
    public Account changeStatus(Long accountId, String newStatus) {
        Account a = getAccount(accountId);
        a.setStatus(newStatus);
        return accountRepository.save(a);
    }

    @Transactional
    public void checkDailyLimit(Account account, BigDecimal amount) {
        if (account.getDailyTransferredAmount().add(amount)
                .compareTo(account.getDailyLimit()) > 0) {
            throw new TransferLimitExceededException("Daily transfer limit exceeded");
        }
    }

    @Transactional
    public void checkMinimumBalance(Account account, BigDecimal amount) {
        if (account.getBalance().subtract(amount)
                .compareTo(account.getMinBalance()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance after transfer");
        }
    }

    public boolean isSuspiciousTransfer(BigDecimal amount) {
        return amount.compareTo(new BigDecimal("10000")) > 0; // example rule
    }
}

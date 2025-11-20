package CoreBankingSimulator.services;

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
        Account a = getAccount(accountId);
        return a.getBalance();
    }

    @Transactional
    public Account changeStatus(Long accountId, String newStatus) {
        Account a = getAccount(accountId);
        a.setStatus(newStatus);
        return accountRepository.save(a);
    }
}

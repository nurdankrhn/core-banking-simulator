package CoreBankingSimulator.controller;

import CoreBankingSimulator.dto.AccountResponse;
import CoreBankingSimulator.dto.CreateAccountRequest;
import CoreBankingSimulator.model.Account;
import CoreBankingSimulator.model.Customer;
import CoreBankingSimulator.services.AccountService;
import CoreBankingSimulator.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CustomerRepository customerRepository;

    public AccountController(AccountService accountService,
                             CustomerRepository customerRepository) {
        this.accountService = accountService;
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest req,
                                                         Authentication auth) {
        // Allow user to create only for themselves unless ADMIN (role checks externally)
        String requester = auth.getName();
        Customer requesterCustomer = customerRepository.findByEmail(requester)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        if (!requesterCustomer.getId().equals(req.getCustomerId()) &&
                !requesterCustomer.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        Account account = accountService.createAccount(req.getCustomerId(), req.getAccountType());
        return ResponseEntity.ok(toDto(account));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccountResponse>> myAccounts(Authentication auth) {
        String email = auth.getName();
        Customer c = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        List<Account> accounts = accountService.listAccountsForCustomer(c.getId());
        List<AccountResponse> resp = accounts.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id, Authentication auth) {
        Account account = accountService.getAccount(id);
        // check ownership or admin
        String email = auth.getName();
        Customer c = customerRepository.findByEmail(email).orElseThrow();
        if (!account.getCustomer().getId().equals(c.getId()) && !c.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(accountService.getBalance(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponse> changeStatus(@PathVariable Long id,
                                                        @RequestParam String status,
                                                        Authentication auth) {
        // only admin
        String email = auth.getName();
        Customer c = customerRepository.findByEmail(email).orElseThrow();
        System.out.println("hereeeeeeeeeeeeeeeeeeeeeeeee " );
        System.out.println("roles: " + c.getRoles());
        if (!c.getRoles().contains("ADMIN")) {
            return ResponseEntity.status(403).build();
        }
        Account updated = accountService.changeStatus(id, status);
        return ResponseEntity.ok(toDto(updated));
    }

    private AccountResponse toDto(Account a) {
        AccountResponse r = new AccountResponse();
        r.setId(a.getId());
        r.setIban(a.getIban());
        r.setAccountType(a.getAccountType());
        r.setBalance(a.getBalance());
        r.setStatus(a.getStatus());
        r.setCreatedAt(a.getCreatedAt());
        return r;
    }
}

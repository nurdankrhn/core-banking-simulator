package CoreBankingSimulator.controller;

import CoreBankingSimulator.model.Transaction;
import CoreBankingSimulator.services.CustomerDetails; // your UserDetails implementation
import CoreBankingSimulator.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // ===================== Deposit =====================
    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<Transaction> deposit(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        Long userId = getCurrentUserId();
        Transaction tx = transactionService.deposit(accountId, amount, description, userId);
        return ResponseEntity.ok(tx);
    }

    // ===================== Withdraw =====================
    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<Transaction> withdraw(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        Long userId = getCurrentUserId();
        Transaction tx = transactionService.withdraw(accountId, amount, description, userId);
        return ResponseEntity.ok(tx);
    }

    // ===================== Transfer =====================
    @PostMapping("/transfer/{accountId}")
    public ResponseEntity<Transaction> transfer(
            @PathVariable Long accountId,
            @RequestParam String targetIban,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String description
    ) {
        Long userId = getCurrentUserId();
        Transaction tx = transactionService.transfer(accountId, targetIban, amount, description, userId);
        return ResponseEntity.ok(tx);
    }

    // ===================== Transaction History =====================
    @GetMapping("/history/{accountId}")
    public ResponseEntity<List<Transaction>> history(@PathVariable Long accountId) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(transactions);
    }

    // ===================== Helper =====================
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerDetails userDetails = (CustomerDetails) authentication.getPrincipal();
        return userDetails.getCustomerId();
    }
}

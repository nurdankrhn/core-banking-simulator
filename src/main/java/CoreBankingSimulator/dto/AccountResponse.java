package CoreBankingSimulator.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class AccountResponse {
    private Long id;
    private String iban;
    private String accountType;
    private BigDecimal balance;
    private String status;
    private OffsetDateTime createdAt;

    // Getters and setters omitted for brevity — add them or use Lombok
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIban() { return iban; }
    public void setIban(String iban) { this.iban = iban; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

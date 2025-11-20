package CoreBankingSimulator.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 34)
    private String iban;

    @Column(nullable = false)
    private String accountType; // CHECKING, SAVINGS

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "ACTIVE"; // ACTIVE, FROZEN, CLOSED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Getters / setters
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
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

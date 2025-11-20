package CoreBankingSimulator.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String type; // DEPOSIT, WITHDRAW, TRANSFER

    @Column
    private String direction; // IN, OUT

    @Column
    private String referenceIban; // for external transfers

    @Column(length = 512)
    private String description;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public String getReferenceIban() { return referenceIban; }
    public void setReferenceIban(String referenceIban) { this.referenceIban = referenceIban; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

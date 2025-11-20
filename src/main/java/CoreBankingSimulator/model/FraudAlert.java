package CoreBankingSimulator.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "fraud_alerts")
public class FraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @Column(nullable = false)
    private String alertType; // HIGH_RISK, LIMIT_EXCEEDED

    private boolean reviewedByAdmin = false;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    // Getters / setters omitted for brevity — add them similar to other entities
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    public boolean isReviewedByAdmin() { return reviewedByAdmin; }
    public void setReviewedByAdmin(boolean reviewedByAdmin) { this.reviewedByAdmin = reviewedByAdmin; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

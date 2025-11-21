package CoreBankingSimulator.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TransactionEvent {
    private Long transactionId;
    private Long accountId;
    private String type;
    private String direction;
    private BigDecimal amount;
    private String description;
    private String status;
    private OffsetDateTime createdAt;
    private Long createdBy;

    // Constructors
    public TransactionEvent() {}

    public TransactionEvent(Long transactionId, Long accountId, String type, String direction,
                            BigDecimal amount, String description, String status,
                            OffsetDateTime createdAt, Long createdBy) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.direction = direction;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }

    // ===================== Getters =====================
    public Long getTransactionId() {
        return transactionId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }
}

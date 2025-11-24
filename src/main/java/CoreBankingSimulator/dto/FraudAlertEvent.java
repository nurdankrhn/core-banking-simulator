package CoreBankingSimulator.dto;

import java.math.BigDecimal;

public class FraudAlertEvent {

    private Long accountId;
    private String iban;
    private BigDecimal amount;
    private String message;

    public FraudAlertEvent() {}

    public FraudAlertEvent(Long accountId, String iban, BigDecimal amount, String message) {
        this.accountId = accountId;
        this.iban = iban;
        this.amount = amount;
        this.message = message;
    }

    public Long getAccountId() { return accountId; }
    public String getIban() { return iban; }
    public BigDecimal getAmount() { return amount; }
    public String getMessage() { return message; }
}

package CoreBankingSimulator.services.kafka;

import CoreBankingSimulator.dto.TransactionEvent;
import CoreBankingSimulator.dto.FraudAlertEvent;
import CoreBankingSimulator.model.Account;
import CoreBankingSimulator.model.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionEventPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String VALIDATED_TOPIC = "transaction.validated";
    private final String POSTED_TOPIC = "transaction.posted";
    private final String FRAUD_TOPIC = "fraud.alert";  // <-- ADD THIS

    public void publishValidated(Transaction tx) {
        sendTransactionEvent(VALIDATED_TOPIC, tx);
    }

    public void publishPosted(Transaction tx) {
        sendTransactionEvent(POSTED_TOPIC, tx);
    }

    private void sendTransactionEvent(String topic, Transaction tx) {
        try {
            TransactionEvent event = new TransactionEvent(
                    tx.getId(),
                    tx.getAccount().getId(),
                    tx.getType(),
                    tx.getDirection(),
                    tx.getAmount(),
                    tx.getDescription(),
                    tx.getStatus(),
                    tx.getCreatedAt(),
                    tx.getCreatedBy()
            );
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void publishFraudAlert(Account account, String message) {
        try {
            FraudAlertEvent alert = new FraudAlertEvent(
                    account.getId(),
                    account.getIban(),
                    null, // amount is optional here, fraud can be about limits
                    message
            );

            String json = objectMapper.writeValueAsString(alert);
            kafkaTemplate.send(FRAUD_TOPIC, json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

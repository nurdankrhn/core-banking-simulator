package CoreBankingSimulator.services.kafka;

import CoreBankingSimulator.dto.TransactionEvent;
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
    private ObjectMapper objectMapper; // For converting Java objects to JSON

    private final String VALIDATED_TOPIC = "transaction.validated";
    private final String POSTED_TOPIC = "transaction.posted";

    public void publishValidated(Transaction tx) {
        sendEvent(VALIDATED_TOPIC, tx);
    }

    public void publishPosted(Transaction tx) {
        sendEvent(POSTED_TOPIC, tx);
    }

    private void sendEvent(String topic, Transaction tx) {
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
}

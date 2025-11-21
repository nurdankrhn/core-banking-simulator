package CoreBankingSimulator.services.kafka;

import CoreBankingSimulator.dto.TransactionEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    // ===================== Fraud Alerts =====================
    @KafkaListener(topics = "fraud.alert", groupId = "banking_group")
    public void consumeFraudAlerts(String message) {
        System.out.println("Fraud alert received: " + message);
        // If JSON, parse it like this:
        // try {
        //     FraudEvent event = objectMapper.readValue(message, FraudEvent.class);
        //     // handle event
        // } catch (JsonProcessingException e) {
        //     e.printStackTrace();
        // }
    }

    // ===================== Notification Events =====================
    @KafkaListener(topics = "notification.events", groupId = "banking_group")
    public void consumeNotificationEvents(String message) {
        System.out.println("Notification event received: " + message);
        // Similar JSON parsing can be done here
    }

    // ===================== Transaction Events =====================
    @KafkaListener(topics = "transaction.validated", groupId = "banking_group")
    public void consumeTransactionValidated(String message) {
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
            System.out.println("Transaction validated: " + event.getTransactionId() +
                    " Amount: " + event.getAmount());
            // Add logic for post-validation actions, e.g., fraud checks
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "transaction.posted", groupId = "banking_group")
    public void consumeTransactionPosted(String message) {
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
            System.out.println("Transaction posted: " + event.getTransactionId() +
                    " Amount: " + event.getAmount());
            // Add logic for notifications, accounting, auditing, etc.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}

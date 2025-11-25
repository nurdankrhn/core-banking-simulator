package CoreBankingSimulator.services.kafka;

import CoreBankingSimulator.dto.NotificationEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationEventPublisher {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final String NOTIFICATION_TOPIC = "notification.events";

    /**
     * Publish a notification event to Kafka.
     */
    public void publishNotification(NotificationEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(NOTIFICATION_TOPIC, json);

            System.out.println("Sent notification event → Customer " + event.getCustomerId());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

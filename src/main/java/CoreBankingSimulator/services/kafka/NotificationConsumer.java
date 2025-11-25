package CoreBankingSimulator.services.kafka;

import CoreBankingSimulator.dto.NotificationEvent;
import CoreBankingSimulator.model.Notification;
import CoreBankingSimulator.repository.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @KafkaListener(topics = "notification.events", groupId = "banking_group")
    public void consumeNotificationEvents(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);

            Notification notification = new Notification();
            notification.setCustomerId(event.getCustomerId());
            notification.setMessage(event.getMessage());
            notification.setType(event.getType());

            notificationRepository.save(notification);

            System.out.println("Saved notification for customer " + event.getCustomerId());

        } catch (Exception e) {
            System.out.println("Error processing notification: " + e.getMessage());
        }
    }
}

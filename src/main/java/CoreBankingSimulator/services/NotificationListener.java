package CoreBankingSimulator.services;

import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import CoreBankingSimulator.dto.NotificationEvent;
import CoreBankingSimulator.model.Notification;
import CoreBankingSimulator.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    private void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "notification.events", groupId = "notification-group")
    public void handleNotification(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);

            Notification n = new Notification();
            n.setCustomerId(event.getCustomerId());
            n.setType(event.getType());
            n.setMessage(event.getMessage());
            notificationRepository.save(n);

            System.out.println("[EMAIL] To: " + event.getEmail()
                    + " | " + event.getType()
                    + ": " + event.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

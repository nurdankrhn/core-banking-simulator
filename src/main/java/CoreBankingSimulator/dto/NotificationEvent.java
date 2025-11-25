package CoreBankingSimulator.dto;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {
    private Long customerId;
    private String email;
    private String type;  // SUCCESS / FAILED / FRAUD
    private String message;
    @CreationTimestamp  @Getter @Setter
    private LocalDateTime createdAt;
}

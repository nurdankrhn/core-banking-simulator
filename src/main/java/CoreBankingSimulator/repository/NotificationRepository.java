package CoreBankingSimulator.repository;

import CoreBankingSimulator.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // get all notifications for a specific customer
    List<Notification> findByCustomerId(Long customerId);

    // optional: find notifications by type (SUCCESS, FAILED, FRAUD_WARNING)
    List<Notification> findByType(String type);
}

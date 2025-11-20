package CoreBankingSimulator.repository;

import CoreBankingSimulator.model.FraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
}

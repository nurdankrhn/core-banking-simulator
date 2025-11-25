package CoreBankingSimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class CoreBankingSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoreBankingSimulatorApplication.class, args);
	}

}

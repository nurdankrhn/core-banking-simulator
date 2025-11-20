package CoreBankingSimulator.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Locale;

@Component
public class IbanGenerator {

    private final SecureRandom rnd = new SecureRandom();

    // Simple TR-like IBAN generator (not production-grade checksum)
    public String generateIban(Long customerId) {
        // Example: TR + 2 digits + 5-digit bank code + 16-digit account number
        String country = "TR";
        int checksum = 10 + rnd.nextInt(90);
        String bankCode = "10001"; // change to your simulated bank code
        String acctNum = String.format("%016d", Math.abs(rnd.nextLong()) % 1_000_000_000_000_000L);
        return country + checksum + bankCode + String.format("%06d", customerId) + acctNum;
    }
}

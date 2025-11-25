package CoreBankingSimulator.repository;

import CoreBankingSimulator.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
    // Find transactions by status
    List<Transaction> findByStatus(String status);

    // Count transactions created on a specific day
    @Query("SELECT t FROM Transaction t WHERE t.createdAt >= :start AND t.createdAt < :end")
    List<Transaction> findByCreatedAtBetween(LocalDate start, LocalDate end);

    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :start AND :end")
    List<Transaction> findByCreatedAtBetween(OffsetDateTime start, OffsetDateTime end);

}

package CoreBankingSimulator.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    private OffsetDateTime expiry;

    // Getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public OffsetDateTime getExpiry() { return expiry; }
    public void setExpiry(OffsetDateTime expiry) { this.expiry = expiry; }
}

package CoreBankingSimulator.controller;

import CoreBankingSimulator.model.Customer;
import CoreBankingSimulator.repository.CustomerRepository;
import CoreBankingSimulator.services.CustomerDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.Set;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder)
    {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Register new customer
    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody Customer customer) {
        // password is added after hashing ve validation
        if(customer.getRoles() == null) {
            customer.setRoles(Set.of("USER"));
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer savedCustomer = customerRepository.save(customer);
        return ResponseEntity.ok(savedCustomer);
    }

    // Get customer by id
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<Customer> getMyProfile(Authentication auth) {

        String email = auth.getName();  // email extracted from JWT

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return ResponseEntity.ok(customer);
    }

    @GetMapping("admin/dashboard")
    public String dashboard() {
        return "ADMIN AREA — ACCESS GRANTED";
    }

}

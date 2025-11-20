package CoreBankingSimulator.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import CoreBankingSimulator.model.Customer;


public class CustomerDetails implements UserDetails {

    private final Customer customer;

    public CustomerDetails(Customer customer) {
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return customer.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getEmail(); // or username field
    }


    public Set<String> getRoles() {
        return customer.getRoles(); // or username field
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public Long getCustomerId() { return customer.getId(); }
}

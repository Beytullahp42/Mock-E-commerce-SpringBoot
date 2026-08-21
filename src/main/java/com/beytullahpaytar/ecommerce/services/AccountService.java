package com.beytullahpaytar.ecommerce.services;

import com.beytullahpaytar.ecommerce.auth.AccountDetails;
import com.beytullahpaytar.ecommerce.models.Account;
import com.beytullahpaytar.ecommerce.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository repository;
    private final PasswordEncoder encoder;

    public AccountService(AccountRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new AccountDetails(account);
    }

    public void addUser(Account account) {
        account.setPassword(encoder.encode(account.getPassword()));
        repository.save(account);
    }
}

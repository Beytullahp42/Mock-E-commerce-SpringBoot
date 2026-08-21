package com.beytullahpaytar.ecommerce.auth;

import com.beytullahpaytar.ecommerce.dto.AuthResponse;
import com.beytullahpaytar.ecommerce.dto.LoginRequest;
import com.beytullahpaytar.ecommerce.dto.MeResponse;
import com.beytullahpaytar.ecommerce.dto.RegisterRequest;
import com.beytullahpaytar.ecommerce.models.Account;
import com.beytullahpaytar.ecommerce.models.Role;
import com.beytullahpaytar.ecommerce.repository.AccountRepository;
import com.beytullahpaytar.ecommerce.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

@Service
public class AuthService {

    private final AccountService accountService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;

    public AuthService(AccountService accountService,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       AccountRepository accountRepository) {
        this.accountService = accountService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = normalize(request.email());
        if (accountRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        Account account = new Account();
        account.setEmail(email);
        account.setPassword(request.password());
        account.setRole(Role.ROLE_USER);

        accountService.addUser(account);

        return login(new LoginRequest(email, request.password()));
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalize(request.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AccountDetails accountDetails = (AccountDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(
                Map.of("role", accountDetails.getRole()),
                accountDetails.getUsername()
        );
        long expiresAt = System.currentTimeMillis() + jwtService.getExpirationMs();

        return new AuthResponse(token, accountDetails.getEmail(), accountDetails.getRole(), expiresAt);
    }

    public MeResponse me(AccountDetails account) {
        return new MeResponse(account.getEmail(), account.getRole());
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}

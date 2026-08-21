package com.beytullahpaytar.ecommerce.auth;

import com.beytullahpaytar.ecommerce.models.Account;
import com.beytullahpaytar.ecommerce.models.Role;
import com.beytullahpaytar.ecommerce.repository.AccountRepository;
import com.beytullahpaytar.ecommerce.services.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountSeeder implements ApplicationRunner {
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public AdminAccountSeeder(AccountRepository accountRepository, AccountService accountService) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (accountRepository.existsByEmail("admin@admin.com")) {
            return;
        }

        Account admin = new Account();
        admin.setEmail("admin@admin.com");
        admin.setPassword("password123");
        admin.setRole(Role.ROLE_ADMIN);
        accountService.addUser(admin);
    }
}

package com.flowiee.dms.config;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.model.UserPrincipal;
import com.flowiee.dms.repository.system.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private final AccountRepository accountRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserPrincipal userPrincipal = (UserPrincipal) event.getAuthentication().getPrincipal();
        Account account = accountRepository.findByUsername(userPrincipal.getUsername());
        if (account != null) {
            account.setFailLogonCount(0);
            accountRepository.save(account);
        }
    }
}
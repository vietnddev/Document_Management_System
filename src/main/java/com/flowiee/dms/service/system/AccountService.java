package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.system.Account;

import java.util.List;

public interface AccountService extends BaseCurdService<Account> {
    List<Account> findAll();

    Account findByUsername(String username);

    Account findCurrentAccount();

    Account getUserByResetTokens(String token);

    void updateTokenForResetPassword(String email, String resetToken);

    void resetPassword(Account account);
}
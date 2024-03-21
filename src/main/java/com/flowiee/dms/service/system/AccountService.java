package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.system.Account;

import java.util.List;

public interface AccountService extends BaseService<Account> {
    List<Account> findAll();

    Account findByUsername(String username);

    Account findCurrentAccount();
}
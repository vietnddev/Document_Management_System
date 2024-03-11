package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.Account;

import java.util.List;

public interface AccountService extends BaseService<Account> {
    List<Account> findAll();

    Account findByUsername(String username);

    Account findCurrentAccount();
}
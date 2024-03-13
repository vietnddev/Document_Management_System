package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.entity.Account;
import com.flowiee.dms.entity.SystemLog;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.repository.AccountRepository;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.service.SystemLogService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired private AccountRepository accountRepo;
    @Autowired private SystemLogService systemLogService;

    @Override
    public List<Account> findAll() {
        return accountRepo.findAll();
    }

    @Override
    public Account findByUsername(String username) {
        return accountRepo.findByUsername(username);
    }

    @Override
    public Account findById(Integer accountId) {
        return accountRepo.findById(accountId).orElse(null);
    }

    @Override
    public Account findCurrentAccount() {
        return this.findById(CommonUtils.getCurrentAccountId());
    }

    @Override
    public Account save(Account account) {
    	try {
            if (account.getRole() != null && account.getRole().equals(CommonUtils.ADMINISTRATOR)) {
                account.setRole("ADMIN");
            } else {
                account.setRole("USER");
            }
            BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
            String password = account.getPassword();
            account.setPassword(bCrypt.encode(password));
            Account accountSaved = accountRepo.save(account);
        	SystemLog systemLog = new SystemLog(AppConstants.SYSTEM_MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_CREATE.name(), "Thêm mới account: " + account.getUsername(), null, CommonUtils.getCurrentAccountId(), CommonUtils.getCurrentAccountIp());
            systemLogService.writeLog(systemLog);
            logger.info("Insert account success! username=" + account.getUsername());
            return accountSaved;
		} catch (Exception e) {
			logger.error("Insert account fail! username=" + account.getUsername(), e);
			throw new AppException();
		}
    }

    @Transactional
    @Override
    public Account update(Account account, Integer entityId) {
    	try {
            account.setId(entityId);
            if (account.getRole() != null && account.getRole().equals(CommonUtils.ADMINISTRATOR)) {
                account.setRole("ADMIN");
            } else {
                account.setRole("USER");
            }
        	SystemLog systemLog = new SystemLog(AppConstants.SYSTEM_MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_UPDATE.name(), "Cập nhật account: " + account.getUsername(), null, CommonUtils.getCurrentAccountId(), CommonUtils.getCurrentAccountIp());
            systemLogService.writeLog(systemLog);
            logger.info("Update account success! username=" + account.getUsername());
            return accountRepo.save(account);
		} catch (Exception e) {
			logger.error("Update account fail! username=" + account.getUsername(), e);
            throw new AppException();
		}
    }

    @Transactional
    @Override
    public String delete(Integer accountId) {
    	Account account = null;
    	try {
            account = accountRepo.findById(accountId).orElse(null);
            if (account != null) {
                accountRepo.delete(account);
                SystemLog systemLog = new SystemLog(AppConstants.SYSTEM_MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_DELETE.name(), "Xóa account " + account.getUsername(), null, CommonUtils.getCurrentAccountId(), CommonUtils.getCurrentAccountIp());
                systemLogService.writeLog(systemLog);
            }
            logger.info("Delete account success! username=" + account.getUsername());
            return MessageUtils.DELETE_SUCCESS;
		} catch (Exception e) {
			logger.error("Delete account fail! username=" + account.getUsername(), e);
			throw new AppException();
		}
    }
}
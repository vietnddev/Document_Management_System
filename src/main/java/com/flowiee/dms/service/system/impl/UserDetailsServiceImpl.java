package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.AuthenticationException;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.AccountRole;
import com.flowiee.dms.entity.system.SystemLog;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.UserPrincipal;
import com.flowiee.dms.repository.system.AccountRepository;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.RoleService;
import com.flowiee.dms.service.system.SystemLogService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, AccountService {
	@Autowired private AccountRepository accountRepo;
	@Autowired private SystemLogService systemLogService;
	@Autowired private RoleService roleService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = this.findByUsername(username);
		UserPrincipal userPrincipal = null;
		if (account != null) {
			userPrincipal = new UserPrincipal(account);

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole());
			grantedAuthorities.add(authority);
			for (AccountRole rights : roleService.findByAccountId(account.getId())) {
				GrantedAuthority rightsAction = new SimpleGrantedAuthority(rights.getAction());
				grantedAuthorities.add(rightsAction);
			}
			userPrincipal.setAuthorities(grantedAuthorities);

			WebAuthenticationDetails details = null;
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null) {
				Object authDetails = authentication.getDetails();
				if (authDetails instanceof WebAuthenticationDetails) {
					details = (WebAuthenticationDetails) authDetails;
				}
			}
			userPrincipal.setIp(details != null ? details.getRemoteAddress() : "unknown");

			SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_LOGIN.name(), "LOGIN", null, account.getId(), details != null ? details.getRemoteAddress() : "unknown");
			systemLog.setCreatedBy(account.getId());
			systemLog.setLastUpdatedBy(account.getUsername());
			systemLogService.writeLog(systemLog);
		} else {
			throw new AuthenticationException("Login fail! Account not found by username=" + username);
		}
		return userPrincipal;
	}

	@Override
	public Optional<Account> findById(Integer accountId) {
		return accountRepo.findById(accountId);
	}

	@Override
	public Account save(Account account) {
		try {
			if (account.getRole() != null && account.getRole().equals(CommonUtils.ADMIN)) {
				account.setRole("ADMIN");
			} else {
				account.setRole("USER");
			}
			BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
			String password = account.getPassword();
			account.setPassword(bCrypt.encode(password));
			Account accountSaved = accountRepo.save(account);
			SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_CREATE.name(), "Thêm mới account: " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
			systemLogService.writeLog(systemLog);
			logger.info("Insert account success! username=" + account.getUsername());
			return accountSaved;
		} catch (RuntimeException ex) {
			throw new AppException("Insert account fail! username=" + account.getUsername(), ex);
		}
	}

	@Transactional
	@Override
	public Account update(Account account, Integer entityId) {
		try {
			account.setId(entityId);
			if (account.getRole() != null && account.getRole().equals(CommonUtils.ADMIN)) {
				account.setRole("ADMIN");
			} else {
				account.setRole("USER");
			}
			SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_UPDATE.name(), "Cập nhật account: " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
			systemLogService.writeLog(systemLog);
			logger.info("Update account success! username=" + account.getUsername());
			return accountRepo.save(account);
		} catch (RuntimeException ex) {
			throw new AppException("Update account fail! username=" + account.getUsername(), ex);
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
				SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_DELETE.name(), "Xóa account " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
				systemLogService.writeLog(systemLog);
			}
			logger.info("Delete account success! id=" + accountId);
			return MessageUtils.DELETE_SUCCESS;
		} catch (RuntimeException ex) {
			throw new AppException("Delete account fail! id=" + accountId, ex);
		}
	}

	@Override
	public List<Account> findAll() {
		return accountRepo.findAll();
	}

	@Override
	public Account findByUsername(String username) {
		return accountRepo.findByUsername(username);
	}

	@Override
	public Account findCurrentAccount() {
		Optional<Account> account = this.findById(CommonUtils.getUserPrincipal().getId());
		if (account.isPresent()) {
			return account.get();
		}
		throw new BadRequestException();
	}
}
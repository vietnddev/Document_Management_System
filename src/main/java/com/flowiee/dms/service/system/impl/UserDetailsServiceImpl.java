package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.exception.AccountLockedException;
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
import com.flowiee.dms.repository.system.SystemLogRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.RoleService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.constants.*;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class UserDetailsServiceImpl extends BaseService implements UserDetailsService, AccountService {
	private final RoleService         roleService;
	private final SystemLogRepository systemLogRepo;
	private final AccountRepository   accountRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = this.findByUsername(username);
		UserPrincipal userPrincipal;
		if (account != null) {
			if (account.isLocked()) {
				throw new AccountLockedException();
			}
			if (account.isPasswordExpired()) {
				throw new AppException("Password has expired for operator " + account.getUsername());
			}

			userPrincipal = new UserPrincipal(account);

			List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
			GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole());
			grantedAuthorities.add(authority);
			for (AccountRole rights : roleService.findByAccountId(account.getId())) {
				GrantedAuthority rightsAction = new SimpleGrantedAuthority(rights.getAction());
				grantedAuthorities.add(rightsAction);
			}
			if (account.getGroupAccount() != null) {
				for (AccountRole right : roleService.findByGroupId(account.getGroupAccount().getId())) {
					grantedAuthorities.add(new SimpleGrantedAuthority(right.getAction()));
				}
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
			userPrincipal.setCreatedBy(account.getId());
			userPrincipal.setLastUpdatedBy(account.getUsername());

			SystemLog systemLog = SystemLog.builder()
					.module(MODULE.SYSTEM.name())
					.function(ACTION.SYS_LOGIN.name())
					.object(MasterObject.Account.name())
					.mode(LogType.LI.name())
					.content(account.getUsername())
					.title("Login")
					.ip(userPrincipal.getIp())
					.account(account)
					.build();
			systemLog.setCreatedBy(account.getId());
			systemLogRepo.save(systemLog);
		} else {
			throw new AuthenticationException("Login fail! Account not found by username is " + username);
		}
		return userPrincipal;
	}

	@Override
	public Optional<Account> findById(Long accountId) {
		return accountRepository.findById(accountId);
	}

	@Override
	public Account save(Account account) {
		try {
			if (account.getRole() != null && account.getRole().equals(AppConstants.ADMINISTRATOR)) {
				account.setRole("ADMIN");
			} else {
				account.setRole("USER");
			}
			BCryptPasswordEncoder bCrypt = new BCryptPasswordEncoder();
			String password = account.getPassword();
			account.setPassword(bCrypt.encode(password));
			Account accountSaved = accountRepository.save(account);
			//SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_CREATE.name(), "Thêm mới account: " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
			//systemLogService.writeLog(systemLog);
			logger.info("Insert account success! username=" + account.getUsername());
			return accountSaved;
		} catch (RuntimeException ex) {
			throw new AppException("Insert account fail! username=" + account.getUsername(), ex);
		}
	}

	@Transactional
	@Override
	public Account update(Account account, Long entityId) {
		try {
			account.setId(entityId);
			if (account.getRole() != null && account.getRole().equals(AppConstants.ADMINISTRATOR)) {
				account.setRole("ADMIN");
			} else {
				account.setRole("USER");
			}
			//SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_UPDATE.name(), "Cập nhật account: " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
			//systemLogService.writeLog(systemLog);
			logger.info("Update account success! username=" + account.getUsername());
			return accountRepository.save(account);
		} catch (RuntimeException ex) {
			throw new AppException("Update account fail! username=" + account.getUsername(), ex);
		}
	}

	@Transactional
	@Override
	public String delete(Long accountId) {
		Account account = null;
		try {
			account = accountRepository.findById(accountId).orElse(null);
			if (account != null) {
				accountRepository.delete(account);
				//SystemLog systemLog = new SystemLog(MODULE.SYSTEM.name(), ACTION.SYS_ACCOUNT_DELETE.name(), "Xóa account " + account.getUsername(), null, CommonUtils.getUserPrincipal().getId(), CommonUtils.getUserPrincipal().getIp());
				//systemLogService.writeLog(systemLog);
			}
			logger.info("Delete account success! id=" + accountId);
			return MessageCode.DELETE_SUCCESS.getDescription();
		} catch (RuntimeException ex) {
			throw new AppException("Delete account fail! id=" + accountId, ex);
		}
	}

	@Override
	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	@Override
	public Account findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public Account findCurrentAccount() {
		Optional<Account> account = this.findById(CommonUtils.getUserPrincipal().getId());
		if (account.isPresent()) {
			return account.get();
		}
		throw new BadRequestException();
	}

	@Override
	public Account getUserByResetTokens(String token) {
		return accountRepository.findByResetTokens(token);
	}

	@Override
	public void updateTokenForResetPassword(String email, String resetToken) {
		Account account = accountRepository.findByEmail(email);
		account.setResetTokens(resetToken);
		accountRepository.save(account);
	}

	@Override
	public void resetPassword(Account account) {
		accountRepository.save(account);
	}
}
package com.flowiee.dms.core;

import com.flowiee.dms.core.exception.AuthenticationException;
import com.flowiee.dms.core.exception.BadRequestException;
import com.flowiee.dms.core.exception.ForbiddenException;
import com.flowiee.dms.utils.CommonUtils;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class BaseAuthorize {
    @SneakyThrows
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException();
        }
        return authentication.isAuthenticated();
    }
    
    protected boolean isAuthorized(String action, boolean throwException) {
        if (isAuthenticated()) {
            if (CommonUtils.ADMINISTRATOR.equals(CommonUtils.getCurrentAccountUsername())) {
                return true;
            }
            if (vldRole(action)) {
                return true;
            } else {
            	if (throwException) {
            		throw new ForbiddenException("You are not authorized to use this function!");
            	}               
            }
        }
        throw new AuthenticationException();
    }

    private static boolean vldRole(String roleKey) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || roleKey.isEmpty()) {
            throw new BadRequestException();
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals(roleKey)) {
                return true;
            }
        }
        return false;
    }
}
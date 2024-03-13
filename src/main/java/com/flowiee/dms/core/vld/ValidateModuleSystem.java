package com.flowiee.dms.core.vld;

import com.flowiee.dms.core.BaseAuthorize;
import com.flowiee.dms.model.ACTION;
import org.springframework.stereotype.Component;

@Component
public class ValidateModuleSystem extends BaseAuthorize {
    public boolean readPermission(boolean throwException) {
        return isAuthorized(ACTION.SYS_ROLE_READ.name(), throwException);
    }

    public boolean readAccount(boolean throwException) {
        return isAuthorized(ACTION.SYS_ACCOUNT_READ.name(), throwException);
    }

    public boolean insertAccount(boolean throwException) {
        return isAuthorized(ACTION.SYS_ACCOUNT_CREATE.name(), throwException);
    }

    public boolean updateAccount(boolean throwException) {
        return isAuthorized(ACTION.SYS_ACCOUNT_UPDATE.name(), throwException);
    }

    public boolean deleteAccount(boolean throwException) {
        return isAuthorized(ACTION.SYS_ACCOUNT_DELETE.name(), throwException);
    }

    public boolean readLog(boolean throwException) {
        return isAuthorized(ACTION.SYS_LOG_READ.name(), throwException);
    }

    public boolean setupConfig(boolean throwException) {
        return isAuthorized("CONFIG", throwException);
    }
}
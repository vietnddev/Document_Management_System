package com.flowiee.dms.validate.authorize.sys;

public interface IVldModuleSystem {
    boolean readPermission(boolean throwException);

    boolean readAccount(boolean throwException);

    boolean insertAccount(boolean throwException);

    boolean updateAccount(boolean throwException);

    boolean deleteAccount(boolean throwException);

    boolean readLog(boolean throwException);

    boolean setupConfig(boolean throwException);
}
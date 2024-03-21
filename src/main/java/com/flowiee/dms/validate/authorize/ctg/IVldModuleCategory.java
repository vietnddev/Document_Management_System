package com.flowiee.dms.validate.authorize.ctg;

public interface IVldModuleCategory {
    boolean readCategory(boolean throwException);

    boolean insertCategory(boolean throwException);

    boolean updateCategory(boolean throwException);

    boolean deleteCategory(boolean throwException);

    boolean importCategory(boolean throwException);
}
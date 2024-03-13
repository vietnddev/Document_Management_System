package com.flowiee.dms.core.vld;

import com.flowiee.dms.core.BaseAuthorize;
import com.flowiee.dms.model.ACTION;
import org.springframework.stereotype.Component;

@Component
public class ValidateModuleStorage extends BaseAuthorize {
    public boolean dashboard(boolean throwException) {
        return isAuthorized(ACTION.STG_DASHBOARD.name(), throwException);
    }

    public boolean readDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_READ.name(), throwException);
    }

    public boolean insertDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_CREATE.name(), throwException);
    }

    public boolean updateDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_UPDATE.name(), throwException);
    }

    public boolean deleteDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_DELETE.name(), throwException);
    }

    public boolean moveDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_MOVE.name(), throwException);
    }

    public boolean copyDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_COPY.name(), throwException);
    }

    public boolean downloadDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_DOWNLOAD.name(), throwException);
    }

    public boolean shareDoc(boolean throwException) {
        return isAuthorized(ACTION.STG_DOC_SHARE.name(), throwException);
    }
}
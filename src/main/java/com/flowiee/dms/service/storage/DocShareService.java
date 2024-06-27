package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocShareModel;

import java.util.List;

public interface DocShareService extends BaseCurdService<DocShare> {
    List<DocShare> findAll();

    List<DocShareModel> findDetailRolesOfDocument(Integer documentId);

    boolean isShared(int documentId, String role);

    void deleteByAccount(Integer accountId);

    void deleteByDocument(Integer documentId);
}
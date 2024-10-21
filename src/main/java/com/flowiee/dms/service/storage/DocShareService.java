package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocShareModel;

import java.util.List;

public interface DocShareService extends BaseCurdService<DocShare> {
    List<DocShare> findAll();

    List<DocShareModel> findDetailRolesOfDocument(Long documentId);

    boolean isShared(long documentId, String role);

    void deleteByAccount(Long accountId);

    void deleteByDocument(Long documentId);

    void deleteAllByDocument(Long documentId);
}
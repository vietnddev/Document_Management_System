package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.DocShare;
import com.flowiee.dms.model.DocShareModel;

import java.util.List;

public interface DocShareService extends BaseService<DocShare> {
    List<DocShare> findAll();

    List<DocShareModel> findDetailRolesOfDocument(Integer documentId);

    boolean isShared(int documentId);

    void deleteByAccount(Integer accountId);

    void deleteByDocument(Integer documentId);
}
package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.DocHistory;

import java.util.List;

public interface DocHistoryService extends BaseService<DocHistory> {
    List<DocHistory> findAll();

    List<DocHistory> findByDocument(Integer documentId);

    List<DocHistory> findByDocData(Integer docDataId);
}
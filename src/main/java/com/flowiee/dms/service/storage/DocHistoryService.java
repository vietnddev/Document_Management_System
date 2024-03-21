package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.storage.DocHistory;

import java.util.List;

public interface DocHistoryService extends BaseService<DocHistory> {
    List<DocHistory> findAll();

    List<DocHistory> findByDocument(Integer documentId);

    List<DocHistory> findByDocData(Integer docDataId);
}
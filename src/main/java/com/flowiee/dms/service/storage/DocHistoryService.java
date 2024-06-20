package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocHistory;

import java.util.List;

public interface DocHistoryService extends BaseCurdService<DocHistory> {
    List<DocHistory> findAll();

    List<DocHistory> findByDocData(Integer docDataId);
}
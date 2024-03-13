package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.DocData;

import java.util.List;

public interface DocDataService extends BaseService<DocData> {
    List<DocData> findByDocField(Integer docFieldId);

    List<DocData> findByDocument(Integer documentId);

    DocData findByFieldIdAndDocId(Integer docFieldId, Integer documentId);
}
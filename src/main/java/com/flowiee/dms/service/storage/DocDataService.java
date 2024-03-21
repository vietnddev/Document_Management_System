package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.storage.DocData;

import java.util.List;

public interface DocDataService extends BaseService<DocData> {
    List<DocData> findByDocField(Integer docFieldId);

    List<DocData> findByDocument(Integer documentId);

    DocData findByFieldIdAndDocId(Integer docFieldId, Integer documentId);

    String update(String value, Integer docDataId);
}
package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocData;

import java.util.List;

public interface DocDataService extends BaseCurdService<DocData> {
    List<DocData> findByDocField(Integer docFieldId);

    List<DocData> findByDocument(Integer documentId);

    DocData findByFieldIdAndDocId(Integer docFieldId, Integer documentId);

    String update(String value, Integer docDataId);

    void deleteAllByDocument(Integer documentId);
}
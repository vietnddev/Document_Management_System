package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocData;

import java.util.List;

public interface DocDataService extends BaseCurdService<DocData> {
    List<DocData> findByDocField(Long docFieldId);

    List<DocData> findByDocument(Long documentId);

    DocData findByFieldIdAndDocId(Long docFieldId, Long documentId);

    String update(String value, Long docDataId);

    void deleteAllByDocument(Long documentId);
}
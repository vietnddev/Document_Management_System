package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;

import java.util.List;

public interface DocActionService {
    DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy);

    String moveDoc(Integer docId, Integer destinationId);

    List<DocShare> shareDoc(Integer docId, List<DocShareModel> accountShares);
}
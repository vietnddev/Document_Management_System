package com.flowiee.dms.service.storage;

import com.flowiee.dms.model.DocMetaModel;

import java.util.List;

public interface DocMetadataService {
    List<DocMetaModel> findMetadata(Integer documentId);

    String updateMetadata(List<DocMetaModel> metaDTOs, Integer documentId);
}
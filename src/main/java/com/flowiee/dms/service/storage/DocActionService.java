package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocActionService {
    int DELETE_NORMAL = 0;
    int DELETE_SCHEDULE = 1;

    DocumentDTO saveDoc(DocumentDTO documentDTO);

    DocumentDTO updateDoc(DocumentDTO data, Long documentId);

    String updateMetadata(List<DocMetaModel> metaDTOs, Long documentId);

    String deleteDoc(Long documentId, boolean isDeleteSubDoc);

    String deleteDoc(Long documentId, boolean isDeleteSubDoc, boolean forceDelete, int modeDelete);

    DocumentDTO copyDoc(Long docId, Long destinationId, String nameCopy);

    String moveDoc(Long docId, Long destinationId);

    List<DocShare> shareDoc(Long docId, List<DocShareModel> accountShares, boolean applyForSubFolder);

    ResponseEntity<InputStreamResource> downloadDoc(long documentId) throws IOException;

    List<DocumentDTO> importDoc(long docParentId, MultipartFile uploadFile, boolean applyRightsParent) throws IOException;

    void restore(long documentId);
}
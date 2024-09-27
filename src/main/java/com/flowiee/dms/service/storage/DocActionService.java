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
    DocumentDTO saveDoc(DocumentDTO documentDTO);

    DocumentDTO updateDoc(DocumentDTO data, Integer documentId);

    String updateMetadata(List<DocMetaModel> metaDTOs, Integer documentId);

    String deleteDoc(Integer documentId, boolean isDeleteSubDoc);

    DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy);

    String moveDoc(Integer docId, Integer destinationId);

    List<DocShare> shareDoc(Integer docId, List<DocShareModel> accountShares, boolean applyForSubFolder);

    ResponseEntity<InputStreamResource> downloadDoc(int documentId) throws IOException;

    ResponseEntity<InputStreamResource> downloadDocs(List<Integer> documentIdList) throws IOException;

    List<DocumentDTO> importDoc(int docParentId, MultipartFile uploadFile, boolean applyRightsParent) throws IOException;
}
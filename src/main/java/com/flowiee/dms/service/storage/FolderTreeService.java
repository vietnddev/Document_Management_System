package com.flowiee.dms.service.storage;

import com.flowiee.dms.model.dto.DocumentDTO;

import java.util.List;

public interface FolderTreeService {
    List<DocumentDTO> getDocumentWithTreeForm(Integer docParentId, boolean isOnlyFolder);

    DocumentDTO findByDocId(int documentId);
}
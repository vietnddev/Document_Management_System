package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface DocumentInfoService {
    Optional<DocumentDTO> findById(Integer id);

    Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Integer parentId, List<Integer> listId, String isFolder, String pTxtSearch);

    List<DocumentDTO> setInfoRights(List<DocumentDTO> documentDTOs);

    List<DocumentDTO> findSubDocByParentId(Integer parentId, Boolean isFolder, boolean fullLevel, boolean onlyBaseInfo);

    List<Document> findByDoctype(Integer docType);

    List<DocumentDTO> findHierarchyOfDocument(Integer documentId, Integer parentId);

    List<DocumentDTO> findSharedDocFromOthers(Integer accountId);

    List<DocMetaModel> findMetadata(Integer documentId);
}
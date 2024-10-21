package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.SummaryQuota;
import com.flowiee.dms.model.dto.DocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface DocumentInfoService {
    Optional<DocumentDTO> findById(Long id);

    Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Long parentId, List<Long> listId, String isFolder, String pTxtSearch, Boolean isDeleted);

    Page<Document> findAllDeletedDocument(int pageSize, int pageNum);

    List<DocumentDTO> setInfoRights(List<DocumentDTO> documentDTOs);

    List<DocumentDTO> findSubDocByParentId(Long parentId, Boolean isFolder, boolean fullLevel, boolean onlyBaseInfo, boolean isDeleted);

    List<Document> findByDoctype(Long docType);

    List<DocumentDTO> findHierarchyOfDocument(Long documentId, Long parentId);

    List<DocumentDTO> findSharedDocFromOthers(Long accountId);

    List<DocMetaModel> findMetadata(Long documentId);

    SummaryQuota getSummaryQuota(int pageSize, int pageNum, String sortBy, Sort.Direction sortMode);

    Page<DocumentDTO> getDocumentsSharedByOthers(int pageSize, int pageNum);
}
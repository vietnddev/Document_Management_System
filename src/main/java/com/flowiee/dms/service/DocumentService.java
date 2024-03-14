package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.DocShare;
import com.flowiee.dms.entity.Document;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DocumentService extends BaseService<Document> {
    Page<Document> findDocuments(Integer pageSize, Integer pageNum, Integer parentId);

    List<DocumentDTO> findFolderByParentId(Integer parentId);

    String updateMetadata(List<DocMetaModel> metaDTOs, Integer documentId);

    List<DocMetaModel> findMetadata(Integer documentId);

    List<Document> findByDoctype(Integer docType);

    DocumentDTO save(DocumentDTO documentDTO);

    DocumentDTO update(DocumentDTO documentDTO, Integer documentId);

    List<DocumentDTO> findHierarchyOfDocument(Integer documentId, Integer parentId);

    List<DocumentDTO> generateFolderTree(Integer parentId);

    DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy);

    String moveDoc(Integer docId, Integer destinationId);

    List<DocShare> shareDoc(Integer docId, Map<Integer, List<String>> accountShares);

    List<DocumentDTO> findSharedDocFromOthers(Integer accountId);
}
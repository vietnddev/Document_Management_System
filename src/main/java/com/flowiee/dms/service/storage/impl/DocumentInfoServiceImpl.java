package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocShareRepository;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.constants.DocRight;
import com.flowiee.dms.utils.constants.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentInfoServiceImpl extends BaseService implements DocumentInfoService {
    EntityManager      entityManager;
    DocShareRepository docShareRepository;
    DocumentRepository documentRepository;

    @Override
    public Optional<DocumentDTO> findById(Integer id) {
        Optional<Document> document = documentRepository.findById(id);
        return document.map(DocumentDTO::fromDocument);
    }

    @Override
    public Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Integer parentId, List<Integer> listId, String isFolder, String pTxtSearch) {
        Pageable pageable = Pageable.unpaged();
        if (pageSize >= 0 && pageNum >= 0) {
            pageable = PageRequest.of(pageNum, pageSize, Sort.by("isFolder", "createdAt").descending());
        }
        Account currentAccount = CommonUtils.getUserPrincipal();
        boolean isAdmin = AppConstants.ADMINISTRATOR.equals(currentAccount.getUsername());
        Page<Document> documents = documentRepository.findAll(pTxtSearch, parentId, currentAccount.getId(), isAdmin, CommonUtils.getUserPrincipal().getId(), null, isFolder, listId, pageable);
        return new PageImpl<>(DocumentDTO.fromDocuments(documents.getContent()), pageable, documents.getTotalElements());
    }

    @Override
    public List<DocumentDTO> setInfoRights(List<DocumentDTO> documentDTOs) {
        for (DocumentDTO d : documentDTOs) {
            List<DocShare> sharesOfDoc = docShareRepository.findByDocAndAccount(d.getId(), CommonUtils.getUserPrincipal().getId(), null);
            for (DocShare ds : sharesOfDoc) {
                if (DocRight.UPDATE.getValue().equals(ds.getRole())) d.setThisAccCanUpdate(true);
                if (DocRight.DELETE.getValue().equals(ds.getRole())) d.setThisAccCanDelete(true);
                if (DocRight.MOVE.getValue().equals(ds.getRole())) d.setThisAccCanMove(true);
                if (DocRight.SHARE.getValue().equals(ds.getRole())) d.setThisAccCanShare(true);
            }
            if (AppConstants.ADMINISTRATOR.equals(CommonUtils.getUserPrincipal().getUsername())) {
                d.setThisAccCanUpdate(true);
                d.setThisAccCanDelete(true);
                d.setThisAccCanMove(true);
                d.setThisAccCanShare(true);
            }
        }
        return documentDTOs;
    }

    @Override
    public List<DocumentDTO> findSubDocByParentId(Integer parentId, Boolean pIsFolder, boolean fullLevel, boolean onlyBaseInfo) {
        String lvIsFolder = null;
        if (pIsFolder != null) {
            lvIsFolder = pIsFolder.booleanValue() ? "Y" : "N";
        }
        List<DocumentDTO> docDTOs = new ArrayList<>();
        if (!fullLevel) {
            docDTOs = this.findDocuments(-1, -1, parentId, null, lvIsFolder, null).getContent();
        } else {
            List<DocumentDTO> subFolderTemps = new ArrayList<>();
            for (DocumentDTO dto : this.findDocuments(-1, -1, parentId, null, lvIsFolder, null).getContent()) {
                if (dto.getIsFolder().equals("Y")) {
                    subFolderTemps.add(dto);
                }
                docDTOs.add(dto);
            }
            for (DocumentDTO tmpFolder : subFolderTemps) {
                docDTOs.addAll(this.findSubDocByParentId(tmpFolder.getId(), null, true, onlyBaseInfo));
            }
        }
        if (!onlyBaseInfo) {
            for (DocumentDTO docDTO : docDTOs) {
                if (docDTO.getIsFolder().equals("N")) {
                    docDTO.setHasSubFolder("N");
                } else {
                    boolean existsSubDocument = documentRepository.existsSubDocument(docDTO.getId());
                    docDTO.setHasSubFolder(existsSubDocument ? "Y" : "N");
                }
            }
        }

        return docDTOs;
    }

    @Override
    public List<Document> findByDoctype(Integer docTypeId) {
        return documentRepository.findAll(null, null, null, true, null, null, null, null, Pageable.unpaged()).getContent();
    }

    @Override
    public List<DocumentDTO> findHierarchyOfDocument(Integer documentId, Integer parentId) {
        List<DocumentDTO> hierarchy = new ArrayList<>();
        String strSQL = "WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, 1 " +
                        "    FROM DOCUMENT " +
                        "    WHERE id = ? " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME ,d.PARENT_ID, dh.H_LEVEL + 1 " +
                        "    FROM DOCUMENT d " +
                        "    INNER JOIN DocumentHierarchy dh ON dh.PARENT_ID = d.id " +
                        "), " +
                        "DocumentToFindParent(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, NULL AS H_LEVEL " +
                        "    FROM DOCUMENT " +
                        "    WHERE ID = ? " +
                        ") " +
                        "SELECT ID, NAME, CONCAT(CONCAT(AS_NAME, '-'), ID) AS AS_NAME, PARENT_ID, H_LEVEL " +
                        "FROM DocumentHierarchy " +
                        "UNION ALL " +
                        "SELECT ID, NAME, CONCAT(CONCAT(AS_NAME, '-'), ID) AS AS_NAME, PARENT_ID, H_LEVEL " +
                        "FROM DocumentToFindParent " +
                        "START WITH ID = ? " +
                        "CONNECT BY PRIOR PARENT_ID = ID " +
                        "ORDER BY H_LEVEL DESC";
        logger.info("Load hierarchy of document (breadcrumb)");
        Query query = entityManager.createNativeQuery(strSQL);
        query.setParameter(1, documentId);
        query.setParameter(2, documentId);
        query.setParameter(3, parentId);
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        DocumentDTO rootHierarchy = new DocumentDTO();
        rootHierarchy.setId(null);
        rootHierarchy.setName("Home");
        rootHierarchy.setAsName("");
        hierarchy.add(rootHierarchy);
        for (Object[] doc : list) {
            DocumentDTO docDTO = new DocumentDTO();
            docDTO.setId(Integer.parseInt(String.valueOf(doc[0])));
            docDTO.setName(String.valueOf(doc[1]));
            docDTO.setAsName(String.valueOf(doc[2]));
            docDTO.setParentId(Integer.parseInt(String.valueOf(doc[3])));
            hierarchy.add(docDTO);
        }
        return hierarchy;
    }

    @Override
    public List<DocumentDTO> findSharedDocFromOthers(Integer accountId) {
        return DocumentDTO.fromDocuments(documentRepository.findWasSharedDoc(accountId));
    }

    @Override
    public List<DocMetaModel> findMetadata(Integer documentId) {
        List<DocMetaModel> listReturn = new ArrayList<>();
        try {
            List<Object[]> listData = documentRepository.findMetadata(documentId);
            if (!listData.isEmpty()) {
                for (Object[] data : listData) {
                    DocMetaModel metadata = new DocMetaModel();
                    metadata.setFieldId(Integer.parseInt(String.valueOf(data[0])));
                    metadata.setFieldName(String.valueOf(data[1]));
                    metadata.setDataId(ObjectUtils.isNotEmpty(data[2]) ? Integer.parseInt(String.valueOf(data[2])) : 0);
                    metadata.setDataValue(ObjectUtils.isNotEmpty(data[3]) ? String.valueOf(data[3]) : null);
                    metadata.setFieldType(String.valueOf(data[4]));
                    metadata.setFieldRequired(String.valueOf(data[5]).equals("1"));
                    listReturn.add(metadata);
                }
            }
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "metadata of document"), ex);
        }
        return listReturn;
    }
}
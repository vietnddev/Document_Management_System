package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.core.exception.BadRequestException;
import com.flowiee.dms.entity.*;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.DocShareRepository;
import com.flowiee.dms.repository.DocumentRepository;
import com.flowiee.dms.service.*;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final String module = MODULE.STORAGE.name();

    @Autowired private DocumentRepository documentRepo;
    @Autowired private DocDataService docDataService;
    @Autowired private EntityManager entityManager;
    @Autowired private SystemLogService systemLogService;
    @Autowired private FileStorageService fileService;
    @Autowired private DocShareService docShareService;
    @Autowired private DocShareRepository docShareRepo;

    @Override
    public Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Integer parentId) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("isFolder", "createdAt").descending());
        boolean isAdmin = CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername());
        Page<Document> documents = documentRepo.findAll(parentId, isAdmin, CommonUtils.getUserPrincipal().getId(), pageable);
        List<DocumentDTO> documentDTOs = DocumentDTO.fromDocuments(documents.getContent());
        //Check the currently logged in account has update (U), delete (D), move (M) or share (S) rights?
        for (DocumentDTO d : documentDTOs) {
            List<DocShare> sharesOfDoc = docShareRepo.findByDocAndAccount(d.getId(), CommonUtils.getUserPrincipal().getId());
            for (DocShare ds : sharesOfDoc) {
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_UPDATE.equals(ds.getRole())) d.setThisAccCanUpdate(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_DELETE.equals(ds.getRole())) d.setThisAccCanDelete(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_MOVE.equals(ds.getRole())) d.setThisAccCanMove(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_SHARE.equals(ds.getRole())) d.setThisAccCanShare(true);
            }
        }
        return new PageImpl<>(documentDTOs, pageable, documents.getTotalElements());
    }

    @Override
    public List<DocumentDTO> findFolderByParentId(Integer parentId) {
        return this.generateFolderTree(parentId);
    }

    @Override
    public Document findById(Integer id) {
        return documentRepo.findById(id).orElse(null);
    }

    @Override
    public Document save(Document document) {
        return null;
    }

    @Override
    public Document update(Document data, Integer documentId) {
        Document document = this.findById(documentId);
        if (document == null) {
            throw new BadRequestException();
        }
        document.setName(data.getName());
        document.setDescription(data.getDescription());
        systemLogService.writeLog(module, ACTION.STG_DOC_UPDATE.name(), "Update document: docId=" + documentId, null);
        logger.info(DocumentServiceImpl.class.getName() + ": Update document docId=" + documentId);
        return documentRepo.save(document);
    }

    @Override
    public String updateMetadata(List<DocMetaModel> metaDTOs, Integer documentId) {
        for (DocMetaModel metaDTO : metaDTOs) {
            if (ObjectUtils.isEmpty(metaDTO.getDataValue())) {
                continue;
            }
            DocData docData = docDataService.findByFieldIdAndDocId(metaDTO.getFieldId(), documentId);
            if (docData != null) {
                docData.setValue(metaDTO.getDataValue());
                docDataService.update(docData, docData.getId());
            } else {
                docData = new DocData();
                docData.setDocField(new DocField(metaDTO.getFieldId()));
                docData.setDocument(new Document(documentId));
                docData.setValue(metaDTO.getDataValue());
                docDataService.save(docData);
            }
        }
        systemLogService.writeLog(module, ACTION.STG_DOC_UPDATE.name(), "Update metadata: docId=" + documentId, null);
        logger.info(DocumentServiceImpl.class.getName() + ": Update metadata docId=" + documentId);
        return MessageUtils.UPDATE_SUCCESS;
    }

    @Transactional
    @Override
    public String delete(Integer documentId) {
        Document document = this.findById(documentId);
        if (document == null) {
            throw new BadRequestException("DocField not found!");
        }
        docShareService.deleteByDocument(documentId);
        documentRepo.deleteById(documentId);
        systemLogService.writeLog(module, ACTION.STG_DOC_DELETE.name(), "Xóa tài liệu: docId=" + documentId, null);
        logger.info(DocumentServiceImpl.class.getName() + ": Delete document docId=" + documentId);
        return MessageUtils.DELETE_SUCCESS;
    }

    @Override
    public List<DocMetaModel> findMetadata(Integer documentId) {
        List<DocMetaModel> listReturn = new ArrayList<>();
        try {
            List<Object[]> listData = documentRepo.findMetadata(documentId);
            if (!listData.isEmpty()) {
                for (Object[] data : listData) {
                    DocMetaModel metadata = new DocMetaModel();
                    metadata.setFieldId(Integer.parseInt(String.valueOf(data[0])));
                    metadata.setFieldName(String.valueOf(data[1]));
                    metadata.setDataId(ObjectUtils.isNotEmpty(data[2]) ? Integer.parseInt(String.valueOf(data[2])) : null);
                    metadata.setDataValue(ObjectUtils.isNotEmpty(data[3]) ? String.valueOf(data[3]) : null);
                    metadata.setFieldType(String.valueOf(data[4]));
                    metadata.setFieldRequired(String.valueOf(data[5]).equals("1"));
                    listReturn.add(metadata);
                }
            }
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "metadata of document"), ex);
        }
        return listReturn;
    }

    @Override
    public List<Document> findByDoctype(Integer docTypeId) {
        return documentRepo.findDocumentByDocTypeId(docTypeId);
    }

    @Override
    public DocumentDTO save(DocumentDTO documentDTO) {
        try {
            Document document = Document.fromDocumentDTO(documentDTO);
            document.setAsName(CommonUtils.generateAliasName(document.getName()));
            if (ObjectUtils.isEmpty(document.getParentId())) {
                document.setParentId(0);
            }
            Document documentSaved = documentRepo.save(document);
            if ("N".equals(document.getIsFolder()) && documentDTO.getFileUpload() != null) {
                fileService.saveFileOfDocument(documentDTO.getFileUpload(), documentSaved.getId());
            }
            List<DocShare> roleSharesOfDocument = docShareRepo.findByDocument(documentSaved.getParentId());
            for (DocShare docShare : roleSharesOfDocument) {
                DocShare roleNew = new DocShare();
                roleNew.setDocument(new Document(documentSaved.getId()));
                roleNew.setAccount(new Account(docShare.getAccount().getId()));
                roleNew.setRole(docShare.getRole());
                docShareService.save(roleNew);
            }
            //docShareService.save();
            systemLogService.writeLog(module, ACTION.STG_DOC_CREATE.name(), "Thêm mới tài liệu: " + DocumentDTO.fromDocument(documentSaved), null);
            logger.info(DocumentServiceImpl.class.getName() + ": Thêm mới tài liệu " + DocumentDTO.fromDocument(documentSaved));
            return DocumentDTO.fromDocument(documentSaved);
        } catch (RuntimeException | IOException ex) {
            throw new AppException(ex);
        }
    }

    @Override
    public DocumentDTO update(DocumentDTO dto, Integer documentId) {
        Document document = this.findById(documentId);
        if (document == null) {
            throw new BadRequestException("Document not found!");
        }
        document.setName(dto.getName());
        document.setAsName(CommonUtils.generateAliasName(dto.getName()));
        document.setDescription(dto.getDescription());
        return DocumentDTO.fromDocument(documentRepo.save(document));
    }

    @Override
    public List<DocumentDTO> findHierarchyOfDocument(Integer documentId, Integer parentId) {
        List<DocumentDTO> hierarchy = new ArrayList<>();
        String strSQL = "WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, 1 " +
                        "    FROM STG_DOCUMENT " +
                        "    WHERE id = ? " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME ,d.PARENT_ID, dh.H_LEVEL + 1 " +
                        "    FROM STG_DOCUMENT d " +
                        "    INNER JOIN DocumentHierarchy dh ON dh.PARENT_ID = d.id " +
                        "), " +
                        "DocumentToFindParent(ID, NAME, AS_NAME, PARENT_ID, H_LEVEL) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, NULL AS H_LEVEL " +
                        "    FROM STG_DOCUMENT " +
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
    public List<DocumentDTO> generateFolderTree(Integer parentId) {
        List<DocumentDTO> folderTree = new ArrayList<>();
        String strSQL = "WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, Path, HierarchyLevel) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, CAST(NAME AS VARCHAR2(4000)) AS Path, 0 AS HierarchyLevel " +
                        "    FROM STG_DOCUMENT " +
                        "    WHERE PARENT_ID = 0 AND IS_FOLDER = 'Y' " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME, d.PARENT_ID, d.IS_FOLDER, dh.Path || '/' || d.NAME || '' || ' ', dh.HierarchyLevel + 1 " +
                        "    FROM STG_DOCUMENT d " +
                        "    INNER JOIN DocumentHierarchy dh ON d.PARENT_ID = dh.ID " +
                        "    WHERE d.IS_FOLDER = 'Y' " +
                        ") " +
                        ", RecursiveHierarchy AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, HierarchyLevel, Path, " +
                        "           ROW_NUMBER() OVER (PARTITION BY SUBSTR(Path, 1, INSTR(Path, '/', -1) - 1) ORDER BY Path) AS RowNumm " +
                        "    FROM DocumentHierarchy " +
                        "), SubFolderList AS ( " +
                        "    SELECT dh.PARENT_ID AS Parent_ID, " +
                        "           LISTAGG(dh.ID, '|') WITHIN GROUP (ORDER BY dh.ID) AS SubFoldersId " +
                        "    FROM " +
                        "        DocumentHierarchy dh " +
                        "    GROUP BY dh.PARENT_ID " +
                        ") " +
                        "SELECT rh.ID, " +
                        "       rh.NAME, " +
                        "       rh.AS_NAME, " +
                        "       rh.PARENT_ID, " +
                        "       CASE WHEN EXISTS (SELECT 1 FROM STG_DOCUMENT sub WHERE sub.PARENT_ID = rh.ID AND sub.IS_FOLDER = 'Y') THEN 'Y' ELSE 'N' END AS Has_SubFolders, " +
                        "       sf.SubFoldersId, " +
                        "       rh.HierarchyLevel, " +
                        "       rh.RowNumm, " +
                        "       RTRIM(rh.Path) as Path " +
                        "FROM RecursiveHierarchy rh " +
                        "LEFT JOIN SubFolderList sf ON rh.ID = sf.Parent_ID " +
                        "WHERE rh.PARENT_ID = ? " +
                        "ORDER BY rh.Path";
        //HierarchyLevel: Thư mục ở cấp thứ mấy
        //RowNumm: Thư mục số mấy của cấp HierarchyLevel
        logger.info("Generate folder tree");
        Query query = entityManager.createNativeQuery(strSQL);
        query.setParameter(1, parentId);
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        for (Object[] doc : list) {
            DocumentDTO docDTO = new DocumentDTO();
            docDTO.setId(Integer.parseInt(String.valueOf(doc[0])));
            docDTO.setName(String.valueOf(doc[1]));
            docDTO.setAsName(String.valueOf(doc[2]));
            docDTO.setParentId(Integer.parseInt(String.valueOf(doc[3])));
            docDTO.setHasSubFolder(String.valueOf(doc[4]));
            folderTree.add(docDTO);
        }

        for (int i = 0; i < folderTree.size(); i++) {
            if (folderTree.get(i).getHasSubFolder().equals("Y")) {
                List<Integer> subFolderIds = new ArrayList<>();
                if (list.get(i)[5] != null) {
                    for (String subId : list.get(i)[5].toString().split("\\|")) {
                        subFolderIds.add(Integer.parseInt(subId));
                    }
                }
                folderTree.get(i).setSubFolders(this.findSubFolders(folderTree, subFolderIds));
            }
        }

        return folderTree;
    }

    private List<DocumentDTO> findSubFolders(List<DocumentDTO> lsFolders, List<Integer> subFolderId) {
        List<DocumentDTO> listSubFolders = new ArrayList<>();
        for (DocumentDTO dto : lsFolders) {
            if (listSubFolders.size() == subFolderId.size()) {
                break;
            }
            if (subFolderId.contains(dto.getId())) {
                listSubFolders.add(dto);
                //System.out.println("Sub " + dto.getName());
            }
        }
        return listSubFolders;
    }

    @Override
    public DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy) {
        Document doc = this.findById(docId);
        //Copy doc
        doc.setId(0);
        doc.setName(nameCopy);
        doc.setAsName(CommonUtils.generateAliasName(nameCopy));
        Document docCopied = this.save(doc);
        //Copy metadata
        for (DocData docData : docDataService.findByDocument(docId)) {
            docData.setId(0);
            docData.setDocument(docCopied);
            docDataService.save(docData);
        }
        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Integer docId, Integer destinationId) {
        documentRepo.updateParentId(destinationId, docId);
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Integer docId, List<DocShareModel> accountShares) {
        Document doc = this.findById(docId);
        if (doc == null || accountShares.isEmpty()) {
            throw new BadRequestException();
        }
        List<DocShare> docShared = new ArrayList<>();
        docShareService.deleteByDocument(docId);
        for (DocShareModel model : accountShares) {
            int accountId = model.getAccountId();
            if (model.getCanRead()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_READ)));
            }
            if (model.getCanUpdate()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_UPDATE)));
            }
            if (model.getCanDelete()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_DELETE)));
            }
            if (model.getCanMove()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_MOVE)));
            }
            if (model.getCanShare()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_SHARE)));
            }
        }
        return docShared;
    }

    @Override
    public List<DocumentDTO> findSharedDocFromOthers(Integer accountId) {
        return DocumentDTO.fromDocuments(documentRepo.findWasSharedDoc(accountId));
    }
}
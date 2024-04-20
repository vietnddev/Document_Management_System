package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocShareRepository;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.storage.DocShareService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.storage.FileStorageService;
import com.flowiee.dms.service.system.SystemLogService;
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
import java.util.Optional;

@Service
public class DocumentInfoServiceImpl implements DocumentInfoService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentInfoServiceImpl.class);

    @Autowired
    private DocumentRepository documentRepo;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SystemLogService systemLogService;
    @Autowired
    private FileStorageService fileService;
    @Autowired
    private DocShareService docShareService;
    @Autowired
    private DocShareRepository docShareRepo;

    @Override
    public Page<DocumentDTO> findDocuments(Integer pageSize, Integer pageNum, Integer parentId, List<Integer> listId, String pTxtSearch) {
        Pageable pageable = Pageable.unpaged();
        if (pageSize >= 0 && pageNum >= 0) {
            pageable = PageRequest.of(pageNum, pageSize, Sort.by("isFolder", "createdAt").descending());
        }
        Account currentAccount = CommonUtils.getUserPrincipal();
        boolean isAdmin = CommonUtils.ADMIN.equals(currentAccount.getUsername());
        Page<Document> documents = documentRepo.findAll(pTxtSearch, parentId, currentAccount.getId(), isAdmin, CommonUtils.getUserPrincipal().getId(), null, listId, pageable);
        List<DocumentDTO> documentDTOs = DocumentDTO.fromDocuments(documents.getContent());
        //Check the currently logged in account has update (U), delete (D), move (M) or share (S) rights?
        for (DocumentDTO d : documentDTOs) {
            List<DocShare> sharesOfDoc = docShareRepo.findByDocAndAccount(d.getId(), CommonUtils.getUserPrincipal().getId());
            for (DocShare ds : sharesOfDoc) {
                if (AppConstants.DOC_RIGHT_UPDATE.equals(ds.getRole())) d.setThisAccCanUpdate(true);
                if (AppConstants.DOC_RIGHT_DELETE.equals(ds.getRole())) d.setThisAccCanDelete(true);
                if (AppConstants.DOC_RIGHT_MOVE.equals(ds.getRole())) d.setThisAccCanMove(true);
                if (AppConstants.DOC_RIGHT_SHARE.equals(ds.getRole())) d.setThisAccCanShare(true);
            }
            if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername())) d.setThisAccCanUpdate(true);
            if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername())) d.setThisAccCanDelete(true);
            if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername())) d.setThisAccCanMove(true);
            if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername())) d.setThisAccCanShare(true);
        }
        return new PageImpl<>(documentDTOs, pageable, documents.getTotalElements());
    }

    @Override
    public List<DocumentDTO> findFolderByParentId(Integer parentId) {
        return this.generateFolderTree(parentId);
    }

    @Override
    public Optional<DocumentDTO> findById(Integer id) {
        Optional<Document> document = documentRepo.findById(id);
        return document.map(d -> Optional.of(DocumentDTO.fromDocument(d))).orElse(null);
    }

    @Override
    public DocumentDTO update(DocumentDTO data, Integer documentId) {
        Optional<Document> document = documentRepo.findById(documentId);
        if (document.isEmpty()) {
            throw new BadRequestException();
        }
        document.get().setName(data.getName());
        document.get().setDescription(data.getDescription());
        systemLogService.writeLog(MODULE.STORAGE.name(), ACTION.STG_DOC_UPDATE.name(), "Update document: docId=" + documentId, null);
        logger.info("{}: Update document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);
        return DocumentDTO.fromDocument(documentRepo.save(document.get()));
    }

    @Transactional
    @Override
    public String delete(Integer documentId) {
        Optional<DocumentDTO> document = this.findById(documentId);
        if (document.isEmpty()) {
            throw new BadRequestException("DocField not found!");
        }
        docShareService.deleteByDocument(documentId);
        documentRepo.deleteById(documentId);
        systemLogService.writeLog(MODULE.STORAGE.name(), ACTION.STG_DOC_DELETE.name(), "Xóa tài liệu: docId=" + documentId, null);
        logger.info("{}: Delete document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);
        return MessageUtils.DELETE_SUCCESS;
    }

    @Override
    public List<Document> findByDoctype(Integer docTypeId) {
        return documentRepo.findAll(null, null, null, true, null, null, null, Pageable.unpaged()).getContent();
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
            systemLogService.writeLog(MODULE.STORAGE.name(), ACTION.STG_DOC_CREATE.name(), "Thêm mới tài liệu: " + DocumentDTO.fromDocument(documentSaved), null);
            logger.info(DocumentInfoServiceImpl.class.getName() + ": Thêm mới tài liệu " + DocumentDTO.fromDocument(documentSaved));
            return DocumentDTO.fromDocument(documentSaved);
        } catch (RuntimeException | IOException ex) {
            throw new AppException(String.format(MessageUtils.CREATE_ERROR_OCCURRED, "document"), ex);
        }
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
    public List<DocumentDTO> generateFolderTree(Integer parentId) {
        List<DocumentDTO> folderTree = new ArrayList<>();
        String strSQL = "WITH DocumentHierarchy(ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, Path, HierarchyLevel) AS ( " +
                        "    SELECT ID, NAME, AS_NAME, PARENT_ID, IS_FOLDER, CAST(NAME AS VARCHAR2(4000)) AS Path, 0 AS HierarchyLevel " +
                        "    FROM DOCUMENT " +
                        "    WHERE PARENT_ID = 0 AND IS_FOLDER = 'Y' " +
                        "    UNION ALL " +
                        "    SELECT d.ID, d.NAME, d.AS_NAME, d.PARENT_ID, d.IS_FOLDER, dh.Path || '/' || d.NAME || '' || ' ', dh.HierarchyLevel + 1 " +
                        "    FROM DOCUMENT d " +
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
                        "       CASE WHEN EXISTS (SELECT 1 FROM DOCUMENT sub WHERE sub.PARENT_ID = rh.ID AND sub.IS_FOLDER = 'Y') THEN 'Y' ELSE 'N' END AS Has_SubFolders, " +
                        "       sf.SubFoldersId, " +
                        "       rh.HierarchyLevel, " +
                        "       rh.RowNumm, " +
                        "       RTRIM(rh.Path) as Path " +
                        "FROM RecursiveHierarchy rh " +
                        "LEFT JOIN SubFolderList sf ON rh.ID = sf.Parent_ID " +
                        "WHERE 1=1 ";
        if (parentId != null) {
            strSQL += "AND rh.PARENT_ID = ? ";
        }
        strSQL += "ORDER BY rh.Path";
        //HierarchyLevel: Thư mục ở cấp thứ mấy
        //RowNumm: Thư mục số mấy của cấp HierarchyLevel
        logger.info("Generate folder tree");
        Query query = entityManager.createNativeQuery(strSQL);
        if (parentId != null) {
            query.setParameter(1, parentId);
        }
        @SuppressWarnings("unchecked")
        List<Object[]> list = query.getResultList();
        for (Object[] doc : list) {
            DocumentDTO docDTO = new DocumentDTO();
            docDTO.setId(Integer.parseInt(String.valueOf(doc[0])));
            docDTO.setName(String.valueOf(doc[1]));
            docDTO.setAsName(String.valueOf(doc[2]));
            docDTO.setParentId(Integer.parseInt(String.valueOf(doc[3])));
            docDTO.setHasSubFolder(String.valueOf(doc[4]));
            docDTO.setPath(String.valueOf(doc[8]));
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
    public List<DocumentDTO> findSharedDocFromOthers(Integer accountId) {
        return DocumentDTO.fromDocuments(documentRepo.findWasSharedDoc(accountId));
    }

    @Override
    public List<DocumentDTO> findVersions(Integer documentId) {
        return null;
    }
}
package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.*;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.ResourceNotFoundException;
import com.flowiee.dms.model.*;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocShareRepository;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.*;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.NotificationService;
import com.flowiee.dms.utils.ChangeLog;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.PdfUtils;
import com.flowiee.dms.utils.constants.DocRight;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.flowiee.dms.utils.constants.MasterObject;
import com.flowiee.dms.utils.constants.MessageCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocActionServiceImpl extends BaseService implements DocActionService {
    AccountService      accountService;
    DocDataService      docDataService;
    DocShareService     docShareService;
    FolderTreeService   folderTreeService;
    FileStorageService  fileStorageService;
    DocumentRepository  documentRepository;
    DocShareRepository  docShareRepository;
    DocumentInfoService documentInfoService;
    NotificationService notificationService;

    @Override
    public DocumentDTO saveDoc(DocumentDTO documentDTO) {
        try {
            Document document = Document.fromDocumentDTO(documentDTO);
            document.setName(document.getName().trim());
            document.setAsName(CommonUtils.generateAliasName(document.getName()));
            if (ObjectUtils.isEmpty(document.getParentId())) {
                document.setParentId(0);
            }
            Document documentSaved = documentRepository.save(document);
            if ("N".equals(document.getIsFolder()) && documentDTO.getFileUpload() != null) {
                fileStorageService.saveFileOfDocument(documentDTO.getFileUpload(), documentSaved.getId());
            }
            List<DocShare> roleSharesOfDocument = docShareRepository.findByDocument(documentSaved.getParentId());
            for (DocShare docShare : roleSharesOfDocument) {
                DocShare roleNew = new DocShare();
                roleNew.setDocument(new Document(documentSaved.getId()));
                roleNew.setAccount(new Account(docShare.getAccount().getId()));
                roleNew.setRole(docShare.getRole());
                docShareService.save(roleNew);
            }
            //docShareService.save();
            systemLogService.writeLogCreate(MODULE.STORAGE, ACTION.STG_DOC_CREATE, MasterObject.Document, "Thêm mới tài liệu", documentSaved.getName());
            logger.info("{}: Thêm mới tài liệu {}", DocumentInfoServiceImpl.class.getName(), DocumentDTO.fromDocument(documentSaved));
            return DocumentDTO.fromDocument(documentSaved);
        } catch (RuntimeException | IOException | DocumentException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR.getDescription(), "document"), ex);
        }
    }

    @Override
    public DocumentDTO updateDoc(DocumentDTO data, Integer documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(documentId, DocRight.UPDATE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        Document documentBefore = ObjectUtils.clone(document.get());

        document.get().setName(data.getName());
        document.get().setDescription(data.getDescription());
        Document documentUpdated = documentRepository.save(document.get());

        ChangeLog changeLog = new ChangeLog(documentBefore, documentUpdated);
        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update document " + document.get().getName(), changeLog);
        logger.info("{}: Update document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);

        return DocumentDTO.fromDocument(documentRepository.save(document.get()));
    }

    @Override
    public String updateMetadata(List<DocMetaModel> metaDTOs, Integer documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }

        for (DocMetaModel metaDTO : metaDTOs) {
            DocData docData = docDataService.findByFieldIdAndDocId(metaDTO.getFieldId(), documentId);
            if (docData != null) {
                docDataService.update(metaDTO.getDataValue(), docData.getId());
            } else {
                docDataService.save(DocData.builder()
                        .docField(new DocField(metaDTO.getFieldId()))
                        .document(new Document(documentId))
                        .value(metaDTO.getDataValue())
                        .build());
            }
        }

        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update metadata of " + document, "-", "-");
        logger.info(DocumentInfoServiceImpl.class.getName() + ": Update metadata docId=" + documentId);

        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public String deleteDoc(Integer documentId) {
        Optional<DocumentDTO> document = documentInfoService.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(documentId, DocRight.DELETE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        List<FileStorage> fileStorages = fileStorageService.findFilesOfDocument(documentId);
        docShareService.deleteByDocument(documentId);
        documentRepository.deleteById(documentId);

        //Delete file on drive
        for (FileStorage fileStorage : fileStorages) {
            String fileExtension = fileStorage.getExtension();
            String filePathStr = CommonUtils.rootPath + "/" + fileStorage.getDirectoryPath() + "/" + fileStorage.getStorageName();
            String filePathTempStr = "";
            if (FileExtension.DOC.key().equals(fileExtension)
                    || FileExtension.DOCX.key().equals(fileExtension)
                    || FileExtension.XLS.key().equals(fileExtension)
                    || FileExtension.XLSX.key().equals(fileExtension)) {
                filePathTempStr = filePathStr.replace("." + fileExtension, ".pdf");
            }
            try {
                Files.deleteIfExists(Paths.get(filePathStr));
                if (!filePathTempStr.equals("")) {
                    Files.deleteIfExists(Paths.get(filePathTempStr));
                }
            } catch (IOException ex) {
                throw new AppException(ex);
            }
        }

        systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_DOC_DELETE, MasterObject.Document, "Xóa tài liệu", document.get().getName());
        logger.info("{}: Delete document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy) {
        Optional<DocumentDTO> doc = documentInfoService.findById(docId);
        if (doc.isEmpty()) {
            throw new BadRequestException("Document to copy not found!");
        }
        if (!docShareService.isShared(docId, DocRight.CREATE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        //Copy doc
        doc.get().setId(null);
        doc.get().setName(nameCopy);
        doc.get().setAsName(CommonUtils.generateAliasName(nameCopy));
        Document docCopied = this.saveDoc(doc.get());
        //Copy metadata
        for (DocData docData : docDataService.findByDocument(docId)) {
            DocData docDataNew = DocData.builder()
                    .docField(docData.getDocField())
                    .document(docCopied)
                    .value(docData.getValue())
                    .build();
            docDataService.save(docDataNew);
        }
        //Copy file attach
        Optional<FileStorage> fileUploaded = fileStorageService.findFileIsActiveOfDocument(docId);
        if (fileUploaded.isPresent()) {
            String newNameFile = CommonUtils.generateUniqueString() + "." + FileUtils.getFileExtension(fileUploaded.get().getStorageName());
            String directoryPath = FileUtils.rootPath + "/" + fileUploaded.get().getDirectoryPath();
            Path pathSrc = Paths.get(directoryPath + "/" + fileUploaded.get().getStorageName());
            Path pathDes = Paths.get(directoryPath + "/" + newNameFile);
            try {
                File fileCloned = Files.copy(pathSrc, pathDes, StandardCopyOption.COPY_ATTRIBUTES).toFile();
                FileStorage fileCloneInfo = FileStorage.builder()
                        .module(MODULE.STORAGE.name())
                        .extension(CommonUtils.getFileExtension(newNameFile))
                        .originalName(newNameFile)
                        .storageName(newNameFile)
                        .fileSize(fileCloned.length())
                        .contentType(Files.probeContentType(pathDes))
                        .directoryPath(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).substring(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).indexOf("uploads")))
                        .account(CommonUtils.getUserPrincipal().toAccountEntity())
                        .isActive(true)
                        .customizeName(newNameFile)
                        .document(docCopied)
                        .build();
                FileStorage fileClonedInfo = fileStorageService.save(fileCloneInfo);

                if (FileExtension.DOC.key().equals(fileUploaded.get().getExtension()) ||
                        FileExtension.DOCX.key().equals(fileUploaded.get().getExtension()) ||
                        FileExtension.XLS.key().equals(fileUploaded.get().getExtension()) ||
                        FileExtension.XLSX.key().equals(fileUploaded.get().getExtension()))
                {
                    PdfUtils.cloneFileToPdf(fileCloned, fileClonedInfo.getExtension());
                }
            } catch (IOException | DocumentException e) {
                logger.error("File to clone does not exist!", e);
            }
        }

        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Integer docId, Integer destinationId) {
        Optional<Document> docToMove = documentRepository.findById(docId);
        if (docToMove.isEmpty()) {
            throw new ResourceNotFoundException("Document to move not found!", false);
        }
        if (documentInfoService.findById(destinationId).isEmpty()) {
            throw new ResourceNotFoundException("Document move to found!", false);
        }
        if (!docShareService.isShared(docId, DocRight.MOVE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        docToMove.get().setParentId(destinationId);
        documentRepository.save(docToMove.get());
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Integer pDocId, List<DocShareModel> accountShares, boolean applyForSubFolder) {
        Optional<DocumentDTO> doc = documentInfoService.findById(pDocId);
        if (doc.isEmpty() || accountShares.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(doc.get().getId(), DocRight.SHARE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        docShareService.deleteAllByDocument(doc.get().getId());
        List<DocShare> docShared = new ArrayList<>();
        for (DocShareModel model : accountShares) {//1 model -> 1 account use document
            Optional<Account> accountOpt = accountService.findById(CommonUtils.getUserPrincipal().getId());
            if (accountOpt.isEmpty()) {
                continue;
            }
            //Share rights to this document and all sub-docs of them
            doShare(doc.get().getId(), model.getAccountId(), model.getCanRead(), model.getCanUpdate(), model.getCanDelete(), model.getCanMove(), model.getCanShare());
            if (applyForSubFolder) {
                if (doc.get().getIsFolder().equals("Y")) {
                    List<DocumentDTO> subDocs = documentInfoService.findSubDocByParentId(doc.get().getId(), null, true, true);
                    for (DocumentDTO dto : subDocs) {
                        doShare(dto.getId(), model.getAccountId(), model.getCanRead(), model.getCanUpdate(),model.getCanDelete(), model.getCanMove(), model.getCanShare());
                    }
                }
            }
            //Notify
            notificationService.save(Notification.builder()
                    .receiver(accountOpt.get())
                    .message(String.format("%s đã chia sẽ cho bạn tài liệu '%s'", accountOpt.get().getFullName(), doc.get().getName()))
                    .build());
        }
        return docShared;
    }

    @Override
    public ResponseEntity<InputStreamResource> downloadDoc(int documentId) throws IOException {
        Optional<DocumentDTO> doc = documentInfoService.findById(documentId);
        if (doc.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(documentId, DocRight.READ.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        File folder = null;
        File fileResponse = null;
        try {
            if (doc.get().getIsFolder().equals("N")) {
                Optional<FileStorage> fileOpt = fileStorageService.findFileIsActiveOfDocument(documentId);
                if (fileOpt.isEmpty()) {
                    throw new ResourceNotFoundException("File attachment of this document does not exist!", false);
                }
                fileResponse = FileUtils.getFileUploaded(fileOpt.get());
                if (!fileResponse.exists()) {
                    throw new ResourceNotFoundException("File attachment of this document does not exist!!", false);
                }
            } else {
                folder = new File(Paths.get(FileUtils.getDownloadStorageTempPath().toString() + "/" + CommonUtils.generateUniqueString()).toUri());
                if (!folder.exists())
                    folder.mkdir();
                List<DocumentDTO> listSubFolderFullLevel = documentInfoService.findSubDocByParentId(documentId, null, true, true);
                for (DocumentDTO docDTO : listSubFolderFullLevel) {
                    DocumentDTO docDTO_ = folderTreeService.findByDocId(docDTO.getId());
                    String path = docDTO_ != null ? docDTO_.getPath() : "";
                    if (docDTO.getIsFolder().equals("Y")) {
                        if (path.contains("/")) {
                            String currentPath = "";
                            for (String s : path.split("/")) {
                                currentPath += s.trim() + "/";
                                Files.createDirectories(Path.of(folder.getPath() + "/" + currentPath));
                            }
                        } else
                            Files.createDirectories(Path.of(folder.getPath() + "/" + path.trim()));
                    } else {
                        Optional<FileStorage> fileStorage = fileStorageService.findFileIsActiveOfDocument(docDTO.getId());
                        if (fileStorage.isPresent()) {
                            File fileUploaded = FileUtils.getFileUploaded(fileStorage.get());
                            if ((fileUploaded != null && fileUploaded.exists()) && (folder != null && folder.exists())) {
                                Path pathSrc = Paths.get(fileUploaded.toURI());
                                Path pathDest = Paths.get(folder.toPath() + "/" + getPathOfFolder(path) + "." + fileStorage.get().getExtension());
                                Files.copy(pathSrc, pathDest, StandardCopyOption.COPY_ATTRIBUTES);
                            }
                        }
                    }
                }
                FileUtils.zipDirectory(folder.getPath(), folder.getPath() + ".zip");
                fileResponse = new File(folder.getPath() + ".zip");
            }
            return ResponseEntity.ok()
                    .headers(CommonUtils.getHttpHeaders(fileResponse.getName()))
                    .body(new InputStreamResource(new FileInputStream(fileResponse)));
        } catch (Exception e) {
            logger.error("Failed to download document!", e);
        } finally {
            if (ObjectUtils.isNotEmpty(folder) && folder.exists())
                FileUtils.deleteDirectory(folder.toPath());
        }
        return null;
    }

    private List<DocShare> doShare(int docId, int accountId, boolean canRead, boolean canUpdate, boolean canDelete, boolean canMove, boolean canShare) {
        List<DocShare> docShared = new ArrayList<>();
        if (canRead) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.READ.getValue())));
        }
        if (canUpdate) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.UPDATE.getValue())));
        }
        if (canDelete) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.DELETE.getValue())));
        }
        if (canMove) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.MOVE.getValue())));
        }
        if (canShare) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.SHARE.getValue())));
        }
        return docShared;
    }

    private String getPathOfFolder(String path) {
        String currentPath = "";
        if (path.contains("/")) {
            for (int i = 0; i < path.split("/").length; i++) {
                if (i < path.split("/").length - 1) {
                    currentPath += path.split("/")[i].trim() + "/";
                } else {
                    currentPath += path.split("/")[i].trim();
                }
            }
        } else {
            currentPath = path;
        }
        return currentPath;
    }
}
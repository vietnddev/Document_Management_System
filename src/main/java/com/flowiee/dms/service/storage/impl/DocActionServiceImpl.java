package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.base.StartUp;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.entity.storage.*;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.entity.system.SystemLog;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.ResourceNotFoundException;
import com.flowiee.dms.model.*;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.repository.storage.*;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.*;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.NotificationService;
import com.flowiee.dms.utils.*;
import com.flowiee.dms.utils.constants.*;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocActionServiceImpl extends BaseService implements DocActionService {
    private final AccountService           accountService;
    private final DocDataService           docDataService;
    private final DocShareService          docShareService;
    private final DocHistoryService        docHistoryService;
    private final FileStorageService       fileStorageService;
    private final DocumentRepository       documentRepository;
    private final DocShareRepository       docShareRepository;
    private final DocumentInfoService      documentInfoService;
    private final NotificationService      notificationService;
    private final StorageHistoryRepository storageHistoryRepository;

    @Transactional
    @Override
    public DocumentDTO saveDoc(DocumentDTO documentDTO) {
        Long lvParentId = documentDTO.getParentId() != null ? documentDTO.getParentId() : 0L;
        String lvIsFolder = documentDTO.getIsFolder();
        String lvDocumentName = CoreUtils.trim(documentDTO.getName());
        String lvDocumentAliasName = FileUtils.generateAliasName(lvDocumentName);
        String lvDescription = CoreUtils.trim(documentDTO.getDescription());
        Long lvDocTypeId = documentDTO.getDocTypeId();

        Document document = Document.builder()
                .parentId(lvParentId)
                .isFolder(lvIsFolder)
                .name(lvDocumentName)
                .asName(lvDocumentAliasName)
                .description(lvDescription)
                .docType(lvDocTypeId != null ? new Category(lvDocTypeId) : null)
                .build();

        if (documentRepository.existsDocument(document.getParentId(), document.getName(), document.getIsFolder())) {
            throw new BadRequestException("A document with the same name already exists.");
        }

        try {
            Document documentSaved = documentRepository.save(document);
            if ("N".equals(document.getIsFolder()) && documentDTO.getFileUpload() != null) {
                fileStorageService.saveFileOfDocument(documentDTO.getFileUpload(), documentSaved.getId());
            }
            List<DocShare> roleSharesOfDocument = docShareRepository.findByDocument(documentSaved.getParentId());
            for (DocShare docShare : roleSharesOfDocument) {
                docShareService.save(DocShare.builder()
                        .document(new Document(documentSaved.getId()))
                        .account(new Account(docShare.getAccount().getId()))
                        .role(docShare.getRole())
                        .build());
            }

            String message = "Thêm mới tài liệu";
            String content = documentSaved.getName();
            if (ACTION.STG_DOC_COPY.name().equals(documentDTO.getAction())) {
                message = "Sao chép tài liệu";
                content = String.format("Sao chép [%s] từ [%s]", content, documentDTO.getCopySourceName());
            }
            systemLogService.writeLogCreate(MODULE.STORAGE, ACTION.STG_DOC_CREATE, MasterObject.Document, message, content);
            docHistoryService.save(StorageHistory.builder()
                    .document(documentSaved)
                    .title("Thêm mới " + documentSaved.getName())
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
            logger.info("{}: {} {}", DocumentInfoServiceImpl.class.getName(), message, DocumentDTO.fromDocument(documentSaved));

            return DocumentDTO.fromDocument(documentSaved);
        } catch (RuntimeException | IOException | DocumentException ex) {
            throw new AppException(String.format(ErrorCode.CREATE_ERROR.getDescription(), "document: ") + ex.getMessage(), ex);
        }
    }

    @Transactional
    @Override
    public DocumentDTO updateDoc(DocumentDTO data, Long documentId) {
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
        docHistoryService.save(documentUpdated, null, null, changeLog, null);
        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update document " + document.get().getName(), changeLog);
        logger.info("{}: Update document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);

        return DocumentDTO.fromDocument(documentRepository.save(document.get()));
    }

    @Transactional
    @Override
    public String updateMetadata(List<DocMetaModel> metaDTOs, Long documentId) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", true);
        }

        for (DocMetaModel metaDTO : metaDTOs) {
            DocData docDataCurrent = docDataService.findByFieldIdAndDocId(metaDTO.getFieldId(), documentId);
            if (docDataCurrent != null) {
                docDataService.update(metaDTO.getDataValue(), docDataCurrent.getId());
            } else {
                docDataService.save(DocData.builder()
                        .docField(new DocField(metaDTO.getFieldId()))
                        .document(new Document(documentId))
                        .value(metaDTO.getDataValue())
                        .build());
            }
            //docHistoryService.saveDocDataHistory(document.get(), docDataCurrent, docDataCurrent.getDocField().getName(), docDataCurrent.getValue(), metaDTO.getDataValue());
        }

        systemLogService.writeLogUpdate(MODULE.STORAGE, ACTION.STG_DOC_UPDATE, MasterObject.Document, "Update metadata of " + document.get().getName(), SystemLog.EMPTY, SystemLog.EMPTY);
        logger.info(DocumentInfoServiceImpl.class.getName() + ": Update metadata docId=" + documentId);

        return MessageCode.UPDATE_SUCCESS.getDescription();
    }

    @Transactional
    @Override
    public String deleteDoc(Long documentId, boolean isDeleteSubDoc) {
        return deleteDoc(documentId, isDeleteSubDoc, false, DELETE_NORMAL);
    }

    @Transactional
    @Override
    public String deleteDoc(Long documentId, boolean isDeleteSubDoc, boolean forceDelete, int modeDelete) {
        Optional<Document> document = documentRepository.findById(documentId);
        if (document.isEmpty()) {
            throw new ResourceNotFoundException("Document not found! " + documentId, false);
        }
        if (DELETE_SCHEDULE != modeDelete) {
            if (!docShareService.isShared(documentId, DocRight.DELETE.getValue())) {
                throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
            }
        }
        if (forceDelete) {
            deleteDoc(documentId);
        } else {
            document.get().setDeletedBy(SecurityUtils.getCurrentUser().getUsername());
            document.get().setDeletedAt(LocalDateTime.now());
            Document documentMovedToTrash = documentRepository.save(document.get());
            docHistoryService.save(StorageHistory.builder()
                    .document(documentMovedToTrash)
                    .title("Chuyển vào thùng rác")
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
        }
        if ("Y".equals(document.get().getIsFolder()) && isDeleteSubDoc) {
            List<DocumentDTO> listSubDocs = documentInfoService.findSubDocByParentId(documentId, null, true, true, false);
            for (DocumentDTO subDoc : listSubDocs) {
                if (forceDelete) {
                    deleteDoc(subDoc.getId());
                } else {
                    Document subDocToDelete = Document.fromDocumentDTO(subDoc);//risk bug
                    subDocToDelete.setDeletedBy(SecurityUtils.getCurrentUser().getUsername());
                    subDocToDelete.setDeletedAt(LocalDateTime.now());
                    Document subDocumentMovedToTrash = documentRepository.save(subDocToDelete);
                    docHistoryService.save(StorageHistory.builder()
                            .document(subDocumentMovedToTrash)
                            .title("Chuyển vào thùng rác")
                            .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                            .build());
                }
            }
        }

        String title = "Di chuyển tài liệu vào thùng rác";
        if (forceDelete)
            title = "Xóa tài liệu";

        if (DELETE_SCHEDULE == modeDelete) {
            SystemLog systemLog = SystemLog.builder().build();
            systemLog.setIp("TP");
            systemLog.setAccount(accountService.findByUsername(AppConstants.ADMINISTRATOR));
            systemLog.setCreatedBy(-1l);
            systemLogService.writeLog(MODULE.STORAGE, ACTION.STG_DOC_DELETE, MasterObject.Document, LogType.D, title, "id=" + documentId, SystemLog.EMPTY, systemLog);
        } else {
            systemLogService.writeLogDelete(MODULE.STORAGE, ACTION.STG_DOC_DELETE, MasterObject.Document, title, "id=" + documentId);
        }
        logger.info("{}: Delete document docId={}", DocumentInfoServiceImpl.class.getName(), documentId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Transactional
    public void deleteDoc(long documentId) {
        deleteFileOfDocument(documentId);
        storageHistoryRepository.deleteAllByDocument(documentId);
        docShareService.deleteByDocument(documentId);
        docDataService.deleteAllByDocument(documentId);
        documentRepository.deleteById(documentId);
    }

    private void deleteFileOfDocument(long documentId) {
        List<FileStorage> fileStorages = fileStorageService.findFilesOfDocument(documentId);
        for (FileStorage fileStorage : fileStorages) {
            String fileExtension = fileStorage.getExtension();
            String filePathStr = "src/main/resources/static" + File.separator + fileStorage.getDirectoryPath() + File.separator + fileStorage.getStorageName();
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
                    //delete .pdf
                    Files.deleteIfExists(Paths.get(filePathTempStr));
                    //delete .png
                    filePathTempStr = filePathTempStr.replace(".pdf", ".png");
                    Files.deleteIfExists(Paths.get(filePathTempStr));
                }
            } catch (IOException ex) {
                throw new AppException(ex);
            }
        }
    }

    @Transactional
    @Override
    public DocumentDTO copyDoc(Long docId, Long destinationId, String nameCopy) {
        Optional<DocumentDTO> doc = documentInfoService.findById(docId);
        if (doc.isEmpty()) {
            throw new BadRequestException("Document to copy not found!");
        }
        if (!docShareService.isShared(docId, DocRight.CREATE.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        if ("Y".equals(doc.get().getIsFolder())) {
            throw new BadRequestException("System does not support copy a document as folder type!");
        }
        //Copy doc
        DocumentDTO docCopy = doc.get();
        docCopy.setId(null);
        docCopy.setAction(ACTION.STG_DOC_COPY.name());
        docCopy.setCopySourceName(docCopy.getName());
        docCopy.setName(nameCopy);
        docCopy.setAsName(FileUtils.generateAliasName(nameCopy));
        Document docCopied = this.saveDoc(docCopy);
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
        Optional<FileStorage> fileUploaded = fileStorageService.getFileActiveOfDocument(docId);
        if (fileUploaded.isPresent()) {
            String newNameFile = generateUniqueStr() + "." + FileUtils.getFileExtension(fileUploaded.get().getStorageName());
            String directoryPath = StartUp.getResourceUploadPath() + File.separator + fileUploaded.get().getDirectoryPath() + File.separator;
            Path pathSrc = Paths.get(directoryPath + fileUploaded.get().getStorageName());
            Path pathDes = Paths.get(directoryPath + newNameFile);
            String pathDesStr = pathDes.toString();
            try {
                FileStorage fileCloneInfo = FileStorage.builder()
                        .module(MODULE.STORAGE.name())
                        .extension(FileUtils.getFileExtension(newNameFile))
                        .originalName(newNameFile)
                        .storageName(newNameFile)
                        .fileSize(pathSrc.toFile().length())
                        .contentType(Files.probeContentType(pathDes))
                        .directoryPath(FileUtils.getUploadPathDir(MODULE.STORAGE.name()).substring(FileUtils.getUploadPathDir(MODULE.STORAGE.name()).indexOf("uploads")))
                        .account(SecurityUtils.getCurrentUser().toAccountEntity())
                        .isActive(true)
                        .customizeName(newNameFile)
                        .document(docCopied)
                        .build();
                FileStorage fileClonedInfo = fileStorageService.save(fileCloneInfo);

                for (FileDTO file : FileUtils.getDocumentFiles(fileUploaded.get())) {
                    if (file.getFile() != null && file.getFile().exists()) {
                        String pathDestinationStr = pathDesStr.replaceAll(FileUtils.getFileExtension(pathDesStr.substring(pathDesStr.lastIndexOf(File.separator) + 1)),
                                FileUtils.getFileExtension(file.getFile().getName()));
                        Path lvPathSource = file.getFile().toPath();
                        Path lvPathDestination = Path.of(pathDestinationStr);
                        Files.copy(lvPathSource, lvPathDestination, StandardCopyOption.COPY_ATTRIBUTES).toFile();
                    }
                }
            } catch (IOException e) {
                logger.error("Copy file attachment failed!", e);
            }
        }

        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Long docId, Long destinationId) {
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
        Document documentMoved = documentRepository.save(docToMove.get());
        docHistoryService.save(StorageHistory.builder()
                .document(documentMoved)
                .title("Di chuyển đến thư mục [" + documentMoved.getName() + "]")
                .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                .build());
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Long pDocId, List<DocShareModel> accountShares, boolean applyForSubFolder) {
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
            Optional<Account> accountOpt = accountService.findById(SecurityUtils.getCurrentUser().getId());
            if (accountOpt.isEmpty()) {
                continue;
            }
            //Share rights to this document and all sub-docs of them
            doShare(doc.get().getId(), model.getAccountId(), model.getCanRead(), model.getCanUpdate(), model.getCanDelete(), model.getCanMove(), model.getCanShare());
            //Share rights to all of sub-docs
            if (applyForSubFolder) {
                if (doc.get().getIsFolder().equals("Y")) {
                    List<DocumentDTO> subDocs = documentInfoService.findSubDocByParentId(doc.get().getId(), null, true, true, false);
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
            docHistoryService.save(StorageHistory.builder()
                    .document(new Document(pDocId))
                    .title("Phân quyền")
                    .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                    .build());
        }
        return docShared;
    }

    private List<DocShare> doShare(long docId, long accountId, boolean canRead, boolean canUpdate, boolean canDelete, boolean canMove, boolean canShare) {
        List<DocShare> docShared = new ArrayList<>();
        if (canRead) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.READ)));
        }
        if (canUpdate) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.UPDATE)));
        }
        if (canDelete) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.DELETE)));
        }
        if (canMove) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.MOVE)));
        }
        if (canShare) {
            docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.SHARE)));
        }
        return docShared;
    }

    @Transactional
    @Override
    public List<DocumentDTO> importDoc(long docParentId, MultipartFile uploadFile, boolean applyRightsParent) throws IOException {
        List<DocumentDTO> listImported = new ArrayList<>();
        if (docParentId > 0) {
            Optional<DocumentDTO> documentOpt = documentInfoService.findById(docParentId);
            if (documentOpt.isEmpty()) {
                throw new ResourceNotFoundException("Document not found!", false);
            }
        }
        //Tạo thư mục tạm lưu file upload
        File folderTemp = Path.of(FileUtils.getSystemPath(SystemPath.ImportStorageTemp) + File.separator + generateUniqueStr()).toFile();
        if (!folderTemp.exists()) folderTemp.mkdirs();
        FolderTree folderTree = null;
        try {
            //Lưu file upload vào thư mục tạm
            Path fileZipUploadedPath = Path.of(folderTemp.getAbsolutePath() + "\\" + uploadFile.getOriginalFilename());
            uploadFile.transferTo(fileZipUploadedPath);
            //Giải nén file zip
            File folderExtracted = FileUtils.unzipDirectory(fileZipUploadedPath.toFile(), null);
            folderTree = FileUtils.buildFolderTree(folderExtracted, 0, docParentId, null);
            //Save to database
            listImported = saveDoc_(folderTree, applyRightsParent);
        } catch (IOException ex) {
            //do something
        } finally {
            if (folderTemp != null) {
                FileUtils.deleteDirectory(folderTemp.toPath());
            }
        }

        List<String> idList = new ArrayList<>();
        for (DocumentDTO dto : listImported)
            idList.add(dto.getId() + "");
        systemLogService.writeLog(MODULE.STORAGE, ACTION.STG_DOC_CREATE, MasterObject.Document, LogType.IM, "Import tài liệu", idList.toArray().toString(), SystemLog.EMPTY);

        return listImported;
    }

    @Transactional
    @Override
    public void restoreTrash(long documentId) {
        Optional<Document> documentOpt = documentRepository.findById(documentId);
        if (documentOpt.isEmpty()) {
            throw new BadRequestException(String.format("Document with id %s not found!", documentId));
        }
        Document document = documentOpt.get();
        if (document.getDeletedAt() == null) {
            throw new BadRequestException(String.format("Document with name %s not in the trash!", document.getName()));
        }
        document.setDeletedAt(null);
        document.setDeletedBy(null);
        Document documentRestored = documentRepository.save(document);
        docHistoryService.save(StorageHistory.builder()
                .document(documentRestored)
                .title("Khôi phục khỏi thùng rác")
                .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                .build());

        if (!document.isFile()) {
            List<DocumentDTO> subDocDTOs = documentInfoService.findSubDocByParentId(documentId, null, true, true, true);
            for (DocumentDTO d : subDocDTOs) {
                documentRepository.setDeleteInformation(d.getId(), null, null);
                docHistoryService.save(StorageHistory.builder()
                        .document(new Document(d.getId()))
                        .title("Khôi phục khỏi thùng rác")
                        .fieldName(StorageHistory.EMPTY).oldValue(StorageHistory.EMPTY).newValue(StorageHistory.EMPTY)
                        .build());
            }
        }
    }

    private List<DocumentDTO> saveDoc_(FolderTree folderTree, boolean applyRightsParent) throws IOException {
        List<DocumentDTO> list = new ArrayList<>();

        DocumentDTO docDTO = new DocumentDTO();
        docDTO.setParentId(folderTree.getParentId());
        docDTO.setIsFolder(folderTree.isDirectory() ? "Y" : "N");
        docDTO.setName(folderTree.getName());
        docDTO.setAsName(FileUtils.generateAliasName(folderTree.getName()));
        docDTO.setFileUpload((!folderTree.isDirectory() && folderTree.getFile() != null) ? FileUtils.convertFileToMultipartFile(folderTree.getFile()) : null);

        DocumentDTO docDTOSaved = saveDoc(docDTO);

        //Phân quyền cho các file được tải lên
        if (applyRightsParent) {
        //    List<DocShareModel> docShareModels = docShareService.findDetailRolesOfDocument(docParentId);
        }

        list.add(docDTOSaved);

        if (folderTree.isDirectory()) {
            for (FolderTree f : folderTree.getSubFiles()) {
                f.setParentId(docDTOSaved.getId());
                if (f.isDirectory()) {
                    if (f.getSubFiles().size() > 0) {
                        saveDoc_(f, applyRightsParent);
                    }
                } else {
                    DocumentDTO docSubDTO = new DocumentDTO();
                    docSubDTO.setParentId(docDTOSaved.getId());
                    docSubDTO.setIsFolder("N");
                    docSubDTO.setName(f.getName());
                    docSubDTO.setAsName(FileUtils.generateAliasName(f.getName()));
                    docSubDTO.setFileUpload((!f.isDirectory() && f.getFile() != null) ? FileUtils.convertFileToMultipartFile(f.getFile()) : null);

                    list.add(saveDoc(docSubDTO));
                }
            }
        }

        return list;
    }

    private String generateUniqueStr() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
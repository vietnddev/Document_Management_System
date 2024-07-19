package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.ResourceNotFoundException;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.FileExtension;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.*;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.NotificationService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.DocRight;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
    FileStorageService  fileStorageService;
    DocumentRepository  documentRepository;
    DocumentInfoService documentInfoService;
    NotificationService notificationService;

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
        Document docCopied = documentInfoService.save(doc.get());
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
                    FileUtils.cloneFileToPdf(fileCloned, fileClonedInfo.getExtension());
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
                    List<DocumentDTO> subDocs = documentInfoService.findSubDocByParentId(doc.get().getId(), null, true);
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
    public ResponseEntity<InputStreamResource> downloadDoc(int documentId) throws FileNotFoundException {
        Optional<DocumentDTO> doc = documentInfoService.findById(documentId);
        if (doc.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!", false);
        }
        if (!docShareService.isShared(documentId, DocRight.READ.getValue())) {
            throw new BadRequestException(ErrorCode.FORBIDDEN_ERROR.getDescription());
        }
        Optional<FileStorage> fileOpt = fileStorageService.findFileIsActiveOfDocument(documentId);
        if (fileOpt.isEmpty()) {
            throw new ResourceNotFoundException("File attachment of this document does not exist!", false);
        }
        File file = FileUtils.getFileUploaded(fileOpt.get());
        if (!file.exists()) {
            throw new ResourceNotFoundException("File attachment of this document does not exist!!", false);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileOpt.get().getStorageName());

        return ResponseEntity.ok().headers(httpHeaders).body(new InputStreamResource(new FileInputStream(file)));
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
}
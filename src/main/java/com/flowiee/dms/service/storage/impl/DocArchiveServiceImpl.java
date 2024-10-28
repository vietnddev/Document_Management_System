package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.base.StartUp;
import com.flowiee.dms.entity.storage.*;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.DocMetaModel;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.repository.storage.*;
import com.flowiee.dms.service.storage.DocArchiveService;
import com.flowiee.dms.service.storage.DocDataService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.storage.FileStorageService;
import com.flowiee.dms.service.system.SystemLogService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.MasterObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocArchiveServiceImpl implements DocArchiveService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DocDataService               docDataService;
    private final SystemLogService             systemLogService;
    private final FileStorageService           fileStorageService;
    private final DocumentInfoService          documentInfoService;
    private final DocumentRepository           documentRepository;
    private final DocVersionRepository         docVersionRepository;
    private final FileStorageRepository        fileStorageRepository;
    private final DocDataHistoryRepository     docDataHistoryRepository;
    private final DocumentHistoryRepository    documentHistoryRepository;
    private final FileStorageHistoryRepository fileStorageHistoryRepository;

    private static final long VERSION_INCREMENT = 1;

    @Override
    public long getNextDocVersion(long documentId) {
        return getLatestDocVersion(documentId) + VERSION_INCREMENT;
    }

    @Override
    public long getLatestDocVersion(long documentId) {
        List<Long> versions = docVersionRepository.getVersions(documentId);
        if (versions == null || versions.isEmpty()) {
            return 0l;
        }
        return versions.get(0);
    }

    @Override
    public void archiveVersion(long pDocumentId, DocVersion pDocVersion) throws IOException {
        Optional<Document> documentOpt = documentRepository.findById(pDocumentId);
        if (documentOpt.isEmpty()) {
            throw new BadRequestException("Document not found!");
        }
        if (!documentOpt.get().isFile()) {
            throw new BadRequestException("Does not support to archive a folder!");
        }
        if (documentOpt.get().getDeletedAt() != null) {
            throw new BadRequestException("Document is in the trash!");
        }
        Document currentDoc = documentOpt.get();
        //Create new version
        String versionName = pDocVersion.getVersionName() != null ? pDocVersion.getVersionName() : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        DocVersion docArchiveVersion = docVersionRepository.save(DocVersion.builder()
                .version(getNextDocVersion(pDocumentId))
                .versionName(versionName)
                .document(currentDoc).build());
        //Archive document info
        documentHistoryRepository.save(DocumentHistory.builder()
                .version(docArchiveVersion.getId())
                .entityId(currentDoc.getId())
                .documentName(currentDoc.getName())
                .asName(currentDoc.getAsName())
                .description(currentDoc.getDescription()).build());
        //Archive metadata
        List<DocMetaModel> metadataModels = documentInfoService.getMetadata(pDocumentId);
        for (DocMetaModel model : metadataModels) {
            docDataHistoryRepository.save(DocDataHistory.builder()
                    .documentId(pDocumentId)
                    .version(docArchiveVersion.getId())
                    .entityId(model.getDataId())
                    .docFieldId(model.getFieldId())
                    .value(model.getDataValue()).build());
        }
        //Clone file attach
        Optional<FileStorage> currentFileOpt = fileStorageService.getFileActiveOfDocument(pDocumentId);
        if (currentFileOpt.isEmpty()) {
            throw new BadRequestException("File model not found!");
        }
        FileStorage currentFileMdl = currentFileOpt.get();
        List<FileDTO> fileList = FileUtils.getDocumentFiles(currentFileMdl);
        if (fileList.isEmpty()) {
            throw new BadRequestException("File attachment not found!!");
        }
        String cloneFileName = CommonUtils.generateUniqueString();
        for (FileDTO file : fileList) {
            String directoryPath = StartUp.getResourceUploadPath() + File.separator + currentFileMdl.getDirectoryPath();
            Path lvPathSource = file.getFile().toPath();
            Path lvPathTarget = Paths.get(directoryPath + File.separator + cloneFileName + "." + FileUtils.getFileExtension(file.getFile().getName()));
            Path lvFileClonedPath = Files.copy(lvPathSource, lvPathTarget, StandardCopyOption.COPY_ATTRIBUTES);
            fileStorageHistoryRepository.save(FileStorageHistory.builder()
                    .version(docArchiveVersion.getId())
                    .documentId(pDocumentId)
                    .entityId(file.getId())
                    .src(lvFileClonedPath.toString())
                    .isMainFile(currentFileMdl.getStorageName().equals(file.getFile().getName()))
                    .build());
        }

        systemLogService.writeLogCreate(MODULE.STORAGE, ACTION.STG_DOC_ARCHIVE, MasterObject.DocVersion, "Lưu trữ phiên bản tài liệu: " + currentDoc.getName(), "Phiên bản: " + docArchiveVersion.getVersionName());
        logger.info("Archived successfully!");
    }

    @Transactional
    @Override
    public void restoreOldVersion(long pDocumentId, long pVersionId) {
        DocVersion docVersionOpt = docVersionRepository.findOldVersion(pDocumentId, pVersionId);
        if (docVersionOpt == null) {
            throw new BadRequestException("Version not found!");
        }
        Optional<Document> documentOpt = documentRepository.findById(pDocumentId);
        if (documentOpt.isEmpty()) {
            throw new BadRequestException("Document not found!");
        }
        if (!documentOpt.get().isFile()) {
            throw new BadRequestException("The system does not support to restore an old version for a folder!");
        }

        Document docOldVersion = docVersionOpt.getDocument();
        Document docCurrentVersion = documentOpt.get();

        docCurrentVersion.setName(docOldVersion.getName());
        docCurrentVersion.setAsName(docOldVersion.getAsName());
        docCurrentVersion.setDescription(docOldVersion.getDescription());
        documentRepository.save(docCurrentVersion);
        logger.info("Revert document info successfully!");

        docDataService.deleteAllByDocument(pDocumentId);
        List<DocDataHistory> docDataHistoryList = docDataHistoryRepository.findByVersion(pDocumentId, pVersionId);
        if (docDataHistoryList != null) {
            for (DocDataHistory model : docDataHistoryList) {
                docDataService.save(DocData.builder()
                        .document(new Document(pDocumentId))
                        .docField(new DocField(model.getDocFieldId()))
                        .value(model.getValue()).build());
            }
        }
        logger.info("Revert document info successfully!");

        List<FileStorageHistory> fileStorageHistoryList = fileStorageHistoryRepository.findOldVersion(pDocumentId, pVersionId);
        if (fileStorageHistoryList != null) {
            for (FileStorageHistory model : fileStorageHistoryList) {
                Optional<FileStorage> lvFileStorageOpt = fileStorageService.findById(model.getEntityId());
                if (lvFileStorageOpt.isEmpty()) {
                    continue;
                }
                FileStorage lvFileStorageMdl = lvFileStorageOpt.get();
                String lvStorageName = model.getSrc().substring(model.getSrc().lastIndexOf(File.separator) + 1);
                String prefixPath = StartUp.getResourceUploadPath().endsWith(File.separator) ? StartUp.getResourceUploadPath() : StartUp.getResourceUploadPath() + File.separator;
                if (model.getSrc().contains(prefixPath)) {
                    model.setSrc(model.getSrc().replaceAll(prefixPath, ""));
                }
                lvFileStorageMdl.setOriginalName(model.getOriginalFileName());
                lvFileStorageMdl.setStorageName(lvStorageName);
                lvFileStorageMdl.setDirectoryPath(model.getSrc());
                lvFileStorageMdl.setActive(model.isMainFile());

                fileStorageRepository.save(lvFileStorageMdl);
            }
        }
        logger.info("Revert document info successfully!");
    }
}
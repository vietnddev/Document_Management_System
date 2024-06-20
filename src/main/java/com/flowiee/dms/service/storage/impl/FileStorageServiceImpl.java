package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.FileStorageRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.storage.FileStorageService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageServiceImpl extends BaseService implements FileStorageService {
    AccountService        accountService;
    DocumentInfoService   documentInfoService;
    FileStorageRepository fileRepository;

    public FileStorageServiceImpl(AccountService accountService, @Lazy DocumentInfoService documentInfoService, FileStorageRepository fileRepository) {
        this.accountService = accountService;
        this.documentInfoService = documentInfoService;
        this.fileRepository = fileRepository;
    }

    @Override
    public Optional<FileStorage> findById(Integer fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public FileStorage save(FileStorage entity) {
        return null;
    }

    @Override
    public FileStorage update(FileStorage entity, Integer entityId) {
        return null;
    }

    @Override
    public FileStorage findFileIsActiveOfDocument(Integer documentId) {
        return fileRepository.findFileOfDocument(documentId, true).get(0);
    }

    @Override
    public List<FileStorage> findFilesOfDocument(Integer documentId) {
        return fileRepository.findFileOfDocument(documentId, null);
    }

    @Override
    public FileStorage saveFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException {
        long currentTime = Instant.now(Clock.systemUTC()).toEpochMilli();
        FileStorage fileInfo = new FileStorage();
        fileInfo.setModule(MODULE.STORAGE.name());
        fileInfo.setOriginalName(fileUpload.getOriginalFilename());
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setStorageName(currentTime + "_" + fileUpload.getOriginalFilename());
        fileInfo.setFileSize(fileUpload.getSize());
        fileInfo.setExtension(CommonUtils.getFileExtension(fileUpload.getOriginalFilename()));
        fileInfo.setContentType(fileUpload.getContentType());
        fileInfo.setDirectoryPath(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).substring(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).indexOf("uploads")));
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setAccount(accountService.findCurrentAccount());
        fileInfo.setActive(true);
        FileStorage fileSaved = fileRepository.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE.name()) + "/" + currentTime + "_" + fileUpload.getOriginalFilename());
        fileUpload.transferTo(path);

        return fileSaved;
    }

    @Override
    public String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException {
        fileRepository.save(fileInfo);
        fileInfo.setStorageName("I_" + fileInfo.getStorageName());
        fileImport.transferTo(Paths.get(CommonUtils.getPathDirectory(fileInfo.getModule()) + "/" + fileInfo.getStorageName()));
        return "OK";
    }

    @Override
    public String changFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException {
        Optional<DocumentDTO> document = documentInfoService.findById(documentId);
        if (document.isEmpty()) {
            throw new BadRequestException();
        }
        //Set inactive cho các version cũ
        List<FileStorage> listDocFile = document.get().getListDocFile();
        for (FileStorage docFile : listDocFile) {
            docFile.setActive(false);
            fileRepository.save(docFile);
        }
        //Save file mới vào hệ thống
        long currentTime = Instant.now(Clock.systemUTC()).toEpochMilli();
        FileStorage fileInfo = new FileStorage();
        fileInfo.setModule(MODULE.STORAGE.name());
        fileInfo.setOriginalName(fileUpload.getOriginalFilename());
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setStorageName(currentTime + "_" + fileUpload.getOriginalFilename());
        fileInfo.setFileSize(fileUpload.getSize());
        fileInfo.setExtension(CommonUtils.getFileExtension(fileUpload.getOriginalFilename()));
        fileInfo.setContentType(fileUpload.getContentType());
        fileInfo.setDirectoryPath(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).substring(CommonUtils.getPathDirectory(MODULE.STORAGE.name()).indexOf("uploads")));
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setAccount(accountService.findCurrentAccount());
        fileInfo.setActive(true);
        fileRepository.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE.name()) + "/" + currentTime + "_" + fileUpload.getOriginalFilename());
        fileUpload.transferTo(path);

        return "OK";
    }

    @Override
    public String delete(Integer fileId) {
        FileStorage fileStorage = fileRepository.findById(fileId).orElse(null);
        fileRepository.deleteById(fileId);
        File file = new File(CommonUtils.rootPath + "/" + fileStorage.getDirectoryPath() + "/" + fileStorage.getStorageName());
        if (file.exists() && file.delete()) {
            return MessageUtils.DELETE_SUCCESS;
        }
        return String.format(MessageUtils.DELETE_ERROR_OCCURRED, "file");
    }
}
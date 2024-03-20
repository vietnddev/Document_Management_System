package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.Document;
import com.flowiee.dms.entity.FileStorage;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.repository.FileStorageRepository;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.service.DocumentService;
import com.flowiee.dms.service.FileStorageService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Autowired private AccountService accountService;
    @Autowired private FileStorageRepository fileRepository;
    @Autowired private DocumentService documentService;

    @Override
    public FileStorage findById(Integer fileId) {
        return fileRepository.findById(fileId).orElse(null);
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
    public FileStorage findFileIsActiveOfDocument(int documentId) {
        return fileRepository.findFileIsActiveOfDocument(documentId, true);
    }

    @Override
    public List<FileStorage> getFileOfDocument(Integer documentId) {
        return fileRepository.findFileOfDocument(documentId);
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
        fileInfo.setExtension(CommonUtils.getExtension(fileUpload.getOriginalFilename()));
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
        Document document = documentService.findById(documentId);
        //Set inactive cho các version cũ
        List<FileStorage> listDocFile = document.getListDocFile();
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
        fileInfo.setExtension(CommonUtils.getExtension(fileUpload.getOriginalFilename()));
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
package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileStorageService extends BaseService<FileStorage> {
    List<FileStorage> getFileOfDocument(Integer documentId);

    FileStorage saveFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException;

    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    String changFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException;

    FileStorage findFileIsActiveOfDocument(int documentId);
}
package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.model.dto.FileDTO;
import com.itextpdf.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface FileStorageService extends BaseCurdService<FileStorage> {
    FileStorage saveFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException, DocumentException;

    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    String changFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException, DocumentException;

    Optional<FileStorage> findFileIsActiveOfDocument(Integer documentId);

    List<FileStorage> findFilesOfDocument(Integer documentId);

    FileDTO getFileDisplay(int documentId);
}
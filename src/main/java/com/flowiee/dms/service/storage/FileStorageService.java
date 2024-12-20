package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.model.dto.FileDTO;
import com.itextpdf.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface FileStorageService extends BaseCurdService<FileStorage> {
    FileStorage saveFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException;

    String saveFileOfImport(MultipartFile fileImport, FileStorage fileInfo) throws IOException;

    String changFileOfDocument(MultipartFile fileUpload, Long documentId) throws IOException, DocumentException;

    Optional<FileStorage> getFileActiveOfDocument(Long documentId);

    List<FileStorage> findFilesOfDocument(Long documentId);

    FileDTO getFileDisplay(long documentId);

    void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException;

    long getTotalMemoryUsed(long accountId);
}
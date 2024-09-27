package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.FileExtension;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.repository.storage.FileStorageRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.storage.FileStorageService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.ImageUtils;
import com.flowiee.dms.utils.PdfUtils;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.flowiee.dms.utils.constants.MessageCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageServiceImpl extends BaseService implements FileStorageService {
    DocumentInfoService   documentInfoService;
    FileStorageRepository fileRepository;

    public FileStorageServiceImpl(@Lazy DocumentInfoService documentInfoService, FileStorageRepository fileRepository) {
        this.documentInfoService = documentInfoService;
        this.fileRepository = fileRepository;
    }

    @Override
    public Optional<FileStorage> findById(Integer fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public FileStorage save(FileStorage entity) {
        return fileRepository.save(entity);
    }

    @Override
    public FileStorage update(FileStorage entity, Integer entityId) {
        entity.setId(entityId);
        return fileRepository.save(entity);
    }

    @Override
    public Optional<FileStorage> findFileIsActiveOfDocument(Integer documentId) {
        List<FileStorage> listFiles = fileRepository.findFileOfDocument(documentId, true);
        if (listFiles != null && !listFiles.isEmpty()) {
            return Optional.of(listFiles.get(0));
        }
        return Optional.empty();
    }

    @Override
    public List<FileStorage> findFilesOfDocument(Integer documentId) {
        return fileRepository.findFileOfDocument(documentId, null);
    }

    @Override
    public FileDTO getFileDisplay(int documentId) {
        Optional<FileStorage> fileStorageOpt = this.findFileIsActiveOfDocument(documentId);
        if (fileStorageOpt.isPresent())
        {
            FileStorage fileStorage = fileStorageOpt.get();
            String extension = "." + fileStorage.getExtension();
            if (FileExtension.DOC.key().equals(fileStorage.getExtension()) || FileExtension.DOCX.key().equals(fileStorage.getExtension()) ||
                    FileExtension.XLS.key().equals(fileStorage.getExtension()) || FileExtension.XLSX.key().equals(fileStorage.getExtension()))
            {
                //pdf
                FileStorage pdfModel = ObjectUtils.clone(fileStorage);
                pdfModel.setStorageName(pdfModel.getStorageName().replaceAll(extension, ".pdf"));
                if (FileUtils.getFileUploaded(pdfModel).exists()) {
                    extension = ".pdf";
                }
                //png
                FileStorage imageModel = ObjectUtils.clone(pdfModel);
                imageModel.setStorageName(imageModel.getStorageName().replaceAll(extension, ".png"));
                if (FileUtils.getFileUploaded(imageModel).exists()) {
                    extension = ".png";
                }
            }
            FileDTO fileDTO = FileDTO.fromFileStorage(fileStorageOpt.get());
            fileDTO.setSrc(fileDTO.getSrc().replace("." + fileDTO.getExtension(), extension));

            return fileDTO;
        }
        return new FileDTO();
    }

    @Override
    public FileStorage saveFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException, DocumentException {
        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE);
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setActive(true);
        FileStorage fileSaved = this.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE.name()) + "/" + fileSaved.getStorageName());
        fileUpload.transferTo(path);

        try {
            PdfUtils.cloneFileToPdf(path.toFile(), fileSaved.getExtension());
        } catch (RuntimeException ex) {
            if (FileExtension.XLSX.key().equals(fileSaved.getExtension())
                    || FileExtension.XLSX.key().equals(fileSaved.getExtension())) {
                ImageUtils.cloneFileToImg(path.toFile(), null);
            }
        }

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
    public String changFileOfDocument(MultipartFile fileUpload, Integer documentId) throws IOException, DocumentException {
        DocumentDTO document = documentInfoService.findById(documentId);
        //Set inactive cho các version cũ
        List<FileStorage> listDocFile = document.getListDocFile();
        for (FileStorage docFile : listDocFile) {
            docFile.setActive(false);
            this.update(docFile, docFile.getId());
        }
        //Save file mới vào hệ thống
        FileStorage fileInfo = new FileStorage(fileUpload, MODULE.STORAGE);
        fileInfo.setCustomizeName(fileUpload.getOriginalFilename());
        fileInfo.setDocument(new Document(documentId));
        fileInfo.setActive(true);
        FileStorage fileSaved = this.save(fileInfo);

        Path path = Paths.get(CommonUtils.getPathDirectory(MODULE.STORAGE.name()) + "/" + fileSaved.getStorageName());
        fileUpload.transferTo(path);

        try {
            PdfUtils.cloneFileToPdf(path.toFile(), fileSaved.getExtension());
        } catch (RuntimeException ex) {
            if (FileExtension.XLSX.key().equals(fileSaved.getExtension())
                    || FileExtension.XLSX.key().equals(fileSaved.getExtension())) {
                ImageUtils.cloneFileToImg(path.toFile(), null);
            }
        }

        return "OK";
    }

    @Override
    public String delete(Integer fileId) {
        Optional<FileStorage> fileStorage = fileRepository.findById(fileId);
        if (fileStorage.isEmpty()) {
            throw new AppException(ErrorCode.DELETE_ERROR.getDescription());
        }
        fileRepository.deleteById(fileId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}
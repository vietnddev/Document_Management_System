package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.base.StartUp;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.FileExtension;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.repository.storage.FileStorageRepository;
import com.flowiee.dms.repository.system.SystemConfigRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.storage.FileStorageService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.ImageUtils;
import com.flowiee.dms.utils.PdfUtils;
import com.flowiee.dms.utils.constants.ConfigCode;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.flowiee.dms.utils.constants.MessageCode;
import com.itextpdf.text.DocumentException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
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
@RequiredArgsConstructor
public class FileStorageServiceImpl extends BaseService implements FileStorageService {
    DocumentInfoService documentInfoService;
    FileStorageRepository fileRepository;
    SystemConfigRepository configRepository;

    @Override
    public Optional<FileStorage> findById(Integer fileId) {
        return fileRepository.findById(fileId);
    }

    @Override
    public FileStorage save(FileStorage fileStorage) {
        FileStorage fileStorageSaved = fileRepository.save(fileStorage);

        vldResourceUploadPath(true);
        Path pathDest = Paths.get(CommonUtils.getPathDirectory(fileStorage.getModule().toUpperCase()) + File.separator + fileStorageSaved.getStorageName());
        try {
            saveFileAttach(fileStorage.getFileAttach(), pathDest);
        } catch (IOException ex) {
            throw new AppException("An error occurred while saving the attachment!", ex);
        }

        return fileStorageSaved;
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
        Optional<DocumentDTO> document = documentInfoService.findById(documentId);
        if (document.isEmpty()) {
            throw new BadRequestException();
        }
        //Set inactive cho các version cũ
        List<FileStorage> listDocFile = document.get().getListDocFile();
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
    public void saveFileAttach(MultipartFile multipartFile, Path dest) throws IOException {
        if (vldResourceUploadPath(true)) {
            multipartFile.transferTo(dest);
        }
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

    private boolean vldResourceUploadPath(boolean throwException) {
        if (StartUp.getResourceUploadPath() == null) {
            SystemConfig resourceUploadPathConfig = configRepository.findByCode(ConfigCode.resourceUploadPath.name());
            if (resourceUploadPathConfig != null && ObjectUtils.isNotEmpty(resourceUploadPathConfig.getValue())) {
                StartUp.mvResourceUploadPath = resourceUploadPathConfig.getValue();
                return true;
            } else {
                if (throwException) {
                    throw new AppException("The uploaded file saving directory is not configured, please try again later!");
                } else {
                    return false;
                }
            }
        }
        return true;
    }
}
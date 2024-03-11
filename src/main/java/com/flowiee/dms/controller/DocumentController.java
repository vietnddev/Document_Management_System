package com.flowiee.dms.controller;

import com.flowiee.dms.core.BaseController;
import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.core.exception.NotFoundException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.utils.MessageUtils;
import com.flowiee.dms.entity.Document;
import com.flowiee.dms.service.DocFieldService;
import com.flowiee.dms.service.DocumentService;
import com.flowiee.dms.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
public class DocumentController extends BaseController {
    private final DocumentService documentService;
    private final DocFieldService docFieldService;
    private final FileStorageService fileStorageService;

    public DocumentController(DocumentService documentService, DocFieldService docFieldService, FileStorageService fileStorageService) {
        this.documentService = documentService;
        this.docFieldService = docFieldService;
        this.fileStorageService = fileStorageService;
    }

    @Operation(summary = "Find all documents")
    @GetMapping("/doc/all")
    public ApiResponse<List<DocumentDTO>> getAllDocuments(@RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageNum") Integer pageNum,
                                                          @RequestParam("parentId") Integer parentId) {
        try {
            if (!super.vldModuleStorage.readDoc(true)) {
                return null;
            }
            Page<Document> documents = documentService.findDocuments(pageSize, pageNum - 1, parentId);
            return ApiResponse.ok(DocumentDTO.fromDocuments(documents.getContent()), pageNum, pageSize, documents.getTotalPages(), documents.getTotalElements());
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "documents"), ex);
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "documents"));
        }
    }

    @Operation(summary = "Create new document")
    @PostMapping("/doc/create")
    public ApiResponse<DocumentDTO> insertNewDoc(@RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
                                                 @RequestParam(value = "docTypeId", required = false) Integer docTypeId,
                                                 @RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "isFolder") String isFolder,
                                                 @RequestParam(value = "parentId") Integer parentId) {
        try {
            if (!super.vldModuleStorage.insertDoc(true)) {
                return null;
             }
            DocumentDTO document = new DocumentDTO();
            document.setParentId(parentId);
            document.setName(name);
            document.setDescription(description);
            document.setIsFolder(isFolder);
            document.setDocTypeId(docTypeId);
            document.setFileUpload(fileUpload);
            return ApiResponse.ok(documentService.save(document));
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.CREATE_ERROR_OCCURRED, "document"), ex);
            throw new AppException(String.format(MessageUtils.CREATE_ERROR_OCCURRED, "document"), ex);
        }
    }

    @Operation(summary = "Find all folders")
    @GetMapping("/doc/folders")
    public ApiResponse<List<DocumentDTO>> getAllFolders(@RequestParam(value = "parentId", required = false) Integer parentId) {
        try {
            if (!super.vldModuleStorage.readDoc(true)) {
                return null;
            }
            return ApiResponse.ok(documentService.findFolderByParentId(parentId));
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "folders"), ex);
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "folders"));
        }
    }

    @Operation(summary = "Delete field")
    @DeleteMapping("/doc/doc-field/delete/{id}")
    public ApiResponse<String> deleteDocField(@PathVariable("id") Integer docFiledId) {
        vldModuleStorage.deleteDoc(true);
        if (docFieldService.findById(docFiledId) == null) {
            throw new NotFoundException("DocField not found!");
        }
        return ApiResponse.ok(docFieldService.delete(docFiledId));
    }

    @Operation(summary = "Find all files of document")
    @GetMapping("/doc/files/{id}")
    public ApiResponse<List<FileDTO>> getAllFilesOfDoc(@PathVariable("id") Integer docId) {
        vldModuleStorage.readDoc(true);
        return ApiResponse.ok(FileDTO.fromFileStorages(fileStorageService.getFileOfDocument(docId)));
    }
}
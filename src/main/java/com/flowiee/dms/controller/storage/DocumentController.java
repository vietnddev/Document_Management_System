package com.flowiee.dms.controller.storage;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.payload.MoveDocumentReq;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.service.storage.DocActionService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.ErrorCode;
import com.flowiee.dms.utils.constants.MessageCode;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentController extends BaseController {
    DocActionService    docActionService;
    DocumentInfoService documentInfoService;

    @Operation(summary = "Find all documents")
    @GetMapping("/doc/all")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<DocumentDTO>> getAllDocuments(@RequestParam("pageSize") Integer pageSize,
                                                          @RequestParam("pageNum") Integer pageNum,
                                                          @RequestParam("parentId") Integer parentId,
                                                          @RequestParam(value = "txtSearch", required = false) String txtSearch) {
        try {
            Page<DocumentDTO> documents = documentInfoService.findDocuments(pageSize, pageNum - 1, parentId, null, null, txtSearch);
            List<DocumentDTO> documentIncludeRights = documentInfoService.setInfoRights(documents.getContent());
            return ApiResponse.ok(documentIncludeRights, pageNum, pageSize, documents.getTotalPages(), documents.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "documents"), ex);
        }
    }

    @Operation(summary = "Create new document")
    @PostMapping("/doc/create")
    @PreAuthorize("@vldModuleStorage.insertDoc(true)")
    public ApiResponse<DocumentDTO> insertNewDoc(@RequestParam(value = "fileUpload", required = false) MultipartFile fileUpload,
                                                 @RequestParam(value = "docTypeId", required = false) Integer docTypeId,
                                                 @RequestParam(value = "name") String name,
                                                 @RequestParam(value = "description", required = false) String description,
                                                 @RequestParam(value = "isFolder") String isFolder,
                                                 @RequestParam(value = "parentId") Integer parentId) {
        if ("N".equals(isFolder)) {
            if (fileUpload.isEmpty()) {
                throw new BadRequestException("File attach does not exists!");
            }
            FileUtils.isAllowUpload(FileUtils.getFileExtension(fileUpload.getOriginalFilename()), true, null);
        }
        DocumentDTO document = new DocumentDTO();
        document.setParentId(parentId);
        document.setName(name);
        document.setDescription(description);
        document.setIsFolder(isFolder);
        document.setDocTypeId(docTypeId);
        document.setFileUpload(fileUpload);
        return ApiResponse.ok(docActionService.saveDoc(document));
    }

    @Operation(summary = "Find all folders")
    @GetMapping("/doc/folders")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<DocumentDTO>> getAllFolders(@RequestParam(value = "parentId", required = false) Integer parentId) {
        try {
            List<DocumentDTO> documentDTOs = documentInfoService.findSubDocByParentId(parentId, true, false, false);
            return ApiResponse.ok(documentDTOs, 1, 100, 100, documentDTOs.size());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "folders"), ex);
        }
    }

    @Operation(summary = "Update document")
    @PutMapping("/doc/update/{id}")
    @PreAuthorize("@vldModuleStorage.updateDoc(true)")
    public ApiResponse<DocumentDTO> updateDoc(@PathVariable("id") Integer docId, @ModelAttribute DocumentDTO documentDTO) {
        return ApiResponse.ok(docActionService.updateDoc(documentDTO, docId));
    }

    @Operation(summary = "Delete document")
    @DeleteMapping("/doc/delete/{id}")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ApiResponse<String> deleteDoc(@PathVariable("id") Integer docId) {
        return ApiResponse.ok(docActionService.deleteDoc(docId, true));
    }

    @Operation(summary = "Delete document")
    @DeleteMapping("/doc/multi-delete")
    @PreAuthorize("@vldModuleStorage.deleteDoc(true)")
    public ApiResponse<String> deleteDoc(@RequestParam(value = "ids") List<Integer> pListOfSelectedDocuments) {
        for (int docId : pListOfSelectedDocuments) {
            docActionService.deleteDoc(docId, true);
        }
        return ApiResponse.ok(MessageCode.DELETE_SUCCESS.getDescription());
    }

    @Operation(summary = "Copy document")
    @PostMapping("/doc/copy/{id}")
    @PreAuthorize("@vldModuleStorage.copyDoc(true)")
    public ApiResponse<DocumentDTO> copyDoc(@PathVariable("id") Integer docId, @RequestParam("nameCopy") String nameCopy) {
        return ApiResponse.ok(docActionService.copyDoc(docId, null, nameCopy));
    }

    @Operation(summary = "Move document")
    @PutMapping("/doc/move/{id}")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ApiResponse<String> moveDoc(@PathVariable("id") Integer docId, @RequestBody MoveDocumentReq request) {
        return ApiResponse.ok(docActionService.moveDoc(docId, request.getDestinationId()));
    }

    @Operation(summary = "Move document")
    @PutMapping("/doc/multi-move")
    @PreAuthorize("@vldModuleStorage.moveDoc(true)")
    public ApiResponse<String> moveDoc(@RequestBody MoveDocumentReq request) {
        for (int docId : request.getSelectedDocuments()) {
            docActionService.moveDoc(docId, request.getDestinationId());
        }
        return ApiResponse.ok(MessageCode.UPDATE_SUCCESS.getDescription());
    }

    @Operation(summary = "Download document")
    @GetMapping("/doc/download/{id}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ResponseEntity<InputStreamResource> downloadDoc(@PathVariable("id") Integer documentId) throws IOException {
        return docActionService.downloadDoc(documentId);
    }

    @Operation(summary = "Import documents")
    @PostMapping("/doc/import/{parentDocId}")
    @PreAuthorize("@vldModuleStorage.insertDoc(true)")
    public ApiResponse<List<DocumentDTO>> importDoc(@PathVariable("parentDocId") int parentDocId,
                                                    @RequestParam(value = "fileUpload") MultipartFile fileUpload,
                                                    @RequestParam(value = "applyRightsParent", required = false) Boolean pApplyRightsParent) throws IOException {
        String fileExtension = FileUtils.getFileExtension(fileUpload.getOriginalFilename());
        if (!"zip".equals(fileExtension)) {
            throw new BadRequestException("Hệ thống chỉ hỗ trợ file .zip cho chức năng import!");
        }
        boolean applyRightsParent = false;
        if (ObjectUtils.isNotEmpty(pApplyRightsParent) && pApplyRightsParent.booleanValue() && parentDocId > 0) applyRightsParent = true;
        return ApiResponse.ok(docActionService.importDoc(parentDocId, fileUpload, applyRightsParent));
    }
}
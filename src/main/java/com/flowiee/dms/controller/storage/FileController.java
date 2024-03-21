package com.flowiee.dms.controller.storage;

import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.dto.FileDTO;
import com.flowiee.dms.service.storage.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
public class FileController {
    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Find all files of document")
    @GetMapping("/doc/files/{id}")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ApiResponse<List<FileDTO>> getAllFilesOfDoc(@PathVariable("id") Integer docId) {
        return ApiResponse.ok(FileDTO.fromFileStorages(fileStorageService.findFilesOfDocument(docId)));
    }
}
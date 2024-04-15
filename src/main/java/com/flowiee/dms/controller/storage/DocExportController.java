package com.flowiee.dms.controller.storage;

import com.flowiee.dms.service.storage.DocExportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.prefix}/stg/doc")
public class DocExportController {
    @Autowired
    private DocExportService docExportService;

    @Operation(summary = "Export data")
    @GetMapping("/export/excel")
    @PreAuthorize("@vldModuleStorage.readDoc(true)")
    public ResponseEntity<?> exportToExcel(@RequestParam(value = "parentId", required = false) Integer pParentId,
                                        @RequestParam(value = "exportAll", required = false) Boolean pExportAll) {
        return docExportService.exportToExcel(pParentId, pExportAll);
    }
}
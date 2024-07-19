package com.flowiee.dms.controller.storage;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.service.storage.DocActionService;
import com.flowiee.dms.service.storage.DocShareService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/stg")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocShareController {
    DocShareService  docShareService;
    DocActionService docActionService;

    @Operation(summary = "Get detail shared role of document")
    @GetMapping("/doc/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ApiResponse<List<DocShareModel>> shareDoc(@PathVariable("id") Integer docId) {
        return ApiResponse.ok(docShareService.findDetailRolesOfDocument(docId));
    }

    @Operation(summary = "Share document")
    @PutMapping("/doc/share/{id}")
    @PreAuthorize("@vldModuleStorage.shareDoc(true)")
    public ApiResponse<List<DocShare>> shareDoc(@PathVariable("id") Integer docId,
                                                @RequestBody List<DocShareModel> accountShares,
                                                @RequestParam(value = "applyForSubFolder", required = false) Boolean applyForSubFolder) {
        if (!applyForSubFolder.booleanValue())
            applyForSubFolder = false;
        return ApiResponse.ok(docActionService.shareDoc(docId, accountShares, applyForSubFolder));
    }
}
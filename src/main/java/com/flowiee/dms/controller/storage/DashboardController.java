package com.flowiee.dms.controller.storage;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.SummaryStorage;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.utils.FileUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.math.BigDecimal;

@RestController
@RequestMapping("${app.api.prefix}/stg/dashboard")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DashboardController extends BaseController {
    DocumentRepository documentRepository;

    @GetMapping
    public ApiResponse<SummaryStorage> getSummaryStorage() {
        double totalSizeDouble = (double) FileUtils.getFolderSize(new File(FileUtils.getFileUploadPath())) / (1024 * 1024);
        String lvTotalSize = BigDecimal.valueOf(totalSizeDouble).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        //--
        int lvTotalFile = 0;
        int lvTotalFolder = 0;
        for (Document d : documentRepository.findAll()) {
            if (d.getDeletedAt() == null) {
                if ("N".equals(d.getIsFolder()))
                    lvTotalFile += 1;
                else
                    lvTotalFolder += 1;
            }
        }
        return ApiResponse.ok(SummaryStorage.builder()
                .totalDocument(lvTotalFolder + lvTotalFile)
                .totalFolder(lvTotalFolder)
                .totalFile(lvTotalFile)
                .totalSize(lvTotalSize + " MB")
                .build());
    }
}
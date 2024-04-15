package com.flowiee.dms.service.storage;

import org.springframework.http.ResponseEntity;

public interface DocExportService {
    ResponseEntity<?> exportToExcel(Integer parentId, Boolean exportAll);
}
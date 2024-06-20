package com.flowiee.dms.service;

import com.flowiee.dms.model.EximModel;
import com.flowiee.dms.utils.constants.TemplateExport;

public interface ExportService {
    EximModel exportToExcel(TemplateExport templateExport, Object pCondition, boolean templateOnly);
}
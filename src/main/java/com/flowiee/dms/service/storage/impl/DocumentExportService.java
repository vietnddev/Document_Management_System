package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.service.BaseExportService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocumentExportService extends BaseExportService {
    DocumentInfoService documentInfoService;

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);

        List<DocumentDTO> listData = documentInfoService.getDocumentWithTreeForm(null);
        List<Integer> idList = listData.stream().map(DocumentDTO::getId).toList(); //.toList() from Jdk version 16
        List<DocumentDTO> listDataFull = documentInfoService.findDocuments(-1, -1, null, null, null, null).getContent();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (int i = 0; i < listData.size(); i++) {
            mvWorkbook.createCellStyle().setWrapText(false);
            XSSFRow row = sheet.createRow(i + 2);
            int col = 0;
            row.createCell(col++).setCellValue(i + 1);
            row.createCell(col++).setCellValue(listData.get(i).getPath());
            row.createCell(col++).setCellValue(listData.get(i).getName());
            row.createCell(col++).setCellValue(listData.get(i).getCreatedAt().format(formatter));
            row.createCell(col).setCellValue(listData.get(i).getCreatedBy());
            setBorderCell(row, 0, col);
        }
        sheet.autoSizeColumn(2);
    }
}
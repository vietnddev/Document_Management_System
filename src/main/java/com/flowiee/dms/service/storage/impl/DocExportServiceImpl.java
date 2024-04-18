package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.service.storage.DocExportService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.ExcelUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocExportServiceImpl implements DocExportService {
    @Autowired
    private DocumentInfoService documentInfoService;

    @Override
    public ResponseEntity<?> exportToExcel(Integer parentId, Boolean exportAll) {
        long exportTime = System.currentTimeMillis();
        String rootPath = CommonUtils.templateExportExcelPath;
        String templateName = "Template_E_Document.xlsx";
        String fileNameReturn = exportTime + "_ListOfDocuments.xlsx";
        try {
            Path templateOriginal = Path.of(rootPath + "/" + templateName);
            Path templateTarget = Path.of(rootPath + "/temp/" + exportTime + "_" + templateName);
            File templateToExport = Files.copy(templateOriginal, templateTarget, StandardCopyOption.REPLACE_EXISTING).toFile();
            XSSFWorkbook workbook = new XSSFWorkbook(templateToExport);
            XSSFSheet sheet = workbook.getSheetAt(0);


            List<DocumentDTO> listData = documentInfoService.generateFolderTree(null);
            List<Integer> idList = listData.stream().map(DocumentDTO::getId).toList(); //.toList() from Jdk version 16
            List<DocumentDTO> listDataFull = documentInfoService.findDocuments(-1, -1, null, idList, null).getContent();

            for (int i = 0; i < listData.size(); i++) {
                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setWrapText(false);
                XSSFRow row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(listData.get(i).getPath());
                row.createCell(2).setCellValue(listData.get(i).getName());
                for (int j = 0; j <= 2; j++) {
                    row.getCell(j).setCellStyle(ExcelUtils.setBorder(cellStyle));
                }
            }
            sheet.autoSizeColumn(2);
            Files.deleteIfExists(templateTarget);
            return new ResponseEntity<>(ExcelUtils.build(workbook), ExcelUtils.setHeaders(fileNameReturn), HttpStatus.OK);
        } catch (IOException | InvalidFormatException ex) {
            throw new RuntimeException(ex);
        }
    }
}
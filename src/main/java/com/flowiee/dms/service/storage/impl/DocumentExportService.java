package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.system.AccountRepository;
import com.flowiee.dms.service.BaseExportService;
import com.flowiee.dms.service.storage.DocShareService;
import com.flowiee.dms.service.storage.FolderTreeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class DocumentExportService extends BaseExportService {
    final DocShareService     docShareService;
    final FolderTreeService   folderTreeService;
    final AccountRepository   accountRepository;

    DateTimeFormatter mvFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void writeData(Object pCondition) {
        XSSFSheet sheet = mvWorkbook.getSheetAt(0);
        List<DocumentDTO> listData = folderTreeService.getDocumentWithTreeForm(null, false);
        for (int i = 0; i < listData.size(); i++) {
            DocumentDTO docDTO = listData.get(i);

            List<DocShareModel> docShareDetail = docShareService.findDetailRolesOfDocument(docDTO.getId());
            String[] authorizedAccounts = this.getAuthorizedAccounts(docShareDetail);
            String lvReadRight = authorizedAccounts[0];
            String lvUpdateRight = authorizedAccounts[1];
            String lvDeleteRight = authorizedAccounts[2];
            String lvMoveRight = authorizedAccounts[3];
            String lvShareRight = authorizedAccounts[4];

            Optional<Account> account = accountRepository.findById(docDTO.getCreatedBy());

            mvWorkbook.createCellStyle().setWrapText(false);
            XSSFRow row = sheet.createRow(i + 3);
            int col = 0;
            row.createCell(col++).setCellValue(i + 1);
            row.createCell(col++).setCellValue(docDTO.getName());
            row.createCell(col++).setCellValue(docDTO.getIsFolder().equals("Y") ? "Thư mục" : "File");
            row.createCell(col++).setCellValue(docDTO.getPath());
            row.createCell(col++).setCellValue(docDTO.getCreatedAt().format(mvFormatter));
            row.createCell(col++).setCellValue(account.isPresent() ? account.get().getFullName() : "-");
            row.createCell(col++).setCellValue(lvReadRight);
            row.createCell(col++).setCellValue(lvUpdateRight);
            row.createCell(col++).setCellValue(lvDeleteRight);
            row.createCell(col++).setCellValue(lvMoveRight);
            row.createCell(col).setCellValue(lvShareRight);
            setBorderCell(row, 0, col);
        }
    }

    private String[] getAuthorizedAccounts(List<DocShareModel> docShareDetail) {
        String readRight = "";
        String updateRight = "";
        String deleteRight = "";
        String moveRight = "";
        String shareRight = "";
        for (DocShareModel model : docShareDetail) {
            if (model.getCanRead())
                readRight += ", " + model.getAccountName();
            if (model.getCanUpdate())
                updateRight += ", " + model.getAccountName();
            if (model.getCanDelete())
                deleteRight += ", " + model.getAccountName();
            if (model.getCanMove())
                moveRight += ", " + model.getAccountName();
            if (model.getCanShare())
                shareRight += ", " + model.getAccountName();
        }
        return new String[] {removePrefix(readRight), removePrefix(updateRight), removePrefix(deleteRight), removePrefix(moveRight), removePrefix(shareRight)};
    }

    private String removePrefix(String str) {
        return str.equals("") ? "" : str.substring(2);
    }
}
package com.flowiee.dms.utils;

import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.model.FileExtension;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javax.mail.MethodNotSupportedException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileUtils {
    public static String rootPath = "src/main/resources/static";
    public static String fileUploadPath = rootPath + "/uploads/";
    public static String initCsvDataPath = rootPath + "/data/csv";
    public static String reportTemplatePath = rootPath + "/report";
    public static String excelTemplatePath = rootPath + "/templates/excel";
    public static Path logoPath = Paths.get(FileUtils.rootPath + "/dist/img/FlowieeLogo.png");

    public static void createCellCombobox(XSSFWorkbook workbook, XSSFSheet sheet, XSSFSheet hsheet, List<String> listValue, int row, int column, String nameName) {
        //Put các tên danh mục vào column trong sheet danh mục ẩn
        for (int i = 0; i < listValue.size(); i++) {
            XSSFRow hideRow = hsheet.getRow(i);
            if (hideRow == null) {
                hideRow = hsheet.createRow(i);
            }
            hideRow.createCell(column).setCellValue(listValue.get(i));
        }

        // Khởi tạo name cho mỗi loại danh mục
        Name namedRange = workbook.createName();
        namedRange.setNameName(nameName);
        String colName = CellReference.convertNumToColString(column);
        namedRange.setRefersToFormula(hsheet.getSheetName() + "!$" + colName + "$1:$" + colName + "$" + listValue.size());

        sheet.autoSizeColumn(column); //Auto điều chỉnh độ rộng cột

        DataValidationHelper validationHelper = new XSSFDataValidationHelper(sheet);
        CellRangeAddressList addressList = new CellRangeAddressList(row, row, column, column); //Tạo dropdownlist cho một cell
        DataValidationConstraint constraint = validationHelper.createFormulaListConstraint(nameName);
        DataValidation dataValidation = validationHelper.createValidation(constraint, addressList);

        dataValidation.setSuppressDropDownArrow(true); //Hiển thị mũi tên xổ xuống để chọn giá trị
        dataValidation.setShowErrorBox(true); //Hiển thị hộp thoại lỗi khi chọn giá trị không hợp lệ
        dataValidation.createErrorBox("Error", "Giá trị đã chọn không hợp lệ!");
        dataValidation.setEmptyCellAllowed(false); //Không cho phép ô trống trong dropdownlist
        dataValidation.setShowPromptBox(true); //Hiển thị hộp nhắc nhở khi người dùng chọn ô
        dataValidation.createPromptBox("Danh mục hệ thống", "Vui lòng chọn giá trị!"); //Tạo hộp thoại nhắc nhở khi click chuột vào cell

        sheet.addValidationData(dataValidation);
    }

    public static File getFileDataCategoryInit() {
        return Paths.get(initCsvDataPath + "/Category.csv").toFile();
    }

    public static String getFileExtension(String fileName) {
        String extension = "";
        if (ObjectUtils.isNotEmpty(fileName)) {
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
                extension = fileName.substring(lastIndex + 1);
            }
        }
        return extension;
    }

    public static File getFileUploaded(FileStorage fileModel) {
        Path path = Paths.get(rootPath + "/" + fileModel.getDirectoryPath() + "/" + fileModel.getStorageName());
        return new File(path.toUri());
    }

    public static File cloneFileToPdf(File file, String extension) throws DocumentException, IOException {
        if (ObjectUtils.isEmpty(file) || ObjectUtils.isEmpty(extension))
            return null;
        if (FileExtension.DOC.key().equals(extension)) {
            return convertDocToPdf(file);
        }
        if (FileExtension.DOCX.key().equals(extension)) {
            return convertDocxToPdf(file);
        }
        if (FileExtension.XLS.key().equals(extension)) {
            return convertXlsToPdf(file);
        }
        if (FileExtension.XLSX.key().equals(extension)) {
            return convertXlsxToPdf(file);
        }
        return null;
    }

    public static File convertDocToPdf(File docFile) throws IOException, DocumentException {
        FileInputStream fis = new FileInputStream(docFile);
        HWPFDocument doc = new HWPFDocument(fis);

        String outputFileName = getOutputFileName(docFile.getName());
        File pdfFile = new File(docFile.getParent(), outputFileName);
        FileOutputStream fos = new FileOutputStream(pdfFile);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, fos);
        pdfDoc.open();

        Range range = doc.getRange();
        for (int i = 0; i < range.numParagraphs(); i++) {
            org.apache.poi.hwpf.usermodel.Paragraph paragraph = range.getParagraph(i);
            pdfDoc.add(new Paragraph(paragraph.text()));
        }

        pdfDoc.close();
        fos.close();
        fis.close();

        return pdfFile;
    }

    public static File convertDocxToPdf(File inputFile) throws IOException, DocumentException {
        FileInputStream fis = new FileInputStream(inputFile);
        XWPFDocument document = new XWPFDocument(fis);

        File pdfOutput = new File(inputFile.getParent(), getOutputFileName(inputFile.getName()));
        FileOutputStream fos = new FileOutputStream(pdfOutput);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, fos);
        pdfDoc.open();

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            pdfDoc.add(new Paragraph(paragraph.getText()));
        }

        pdfDoc.close();
        document.close();
        fos.close();
        fis.close();

        return pdfOutput;
    }

    public static File convertXlsToPdf(File excelInput) throws IOException, DocumentException {
        FileInputStream inputStream = new FileInputStream(excelInput);
        Workbook workbook = WorkbookFactory.create(inputStream);

        File pdfOutput = new File(excelInput.getParent(), getOutputFileName(excelInput.getName()));
        FileOutputStream outputStream = new FileOutputStream(pdfOutput);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);

            // Check if sheet is empty or null
            if (sheet == null || sheet.getLastRowNum() == -1) {
                // Handle empty sheet case (skip or throw exception)
                continue; // Or handle as per your application's logic
            }
            int maxColumns = getMaxColumns(sheet);
            PdfPTable table = new PdfPTable(maxColumns);

            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        Cell cell = row.getCell(colNum);
                        if (cell != null) {
                            table.addCell(cell.toString());
                        }
                    }
                }
            }

            document.add(table);
        }

        document.close();
        workbook.close();
        outputStream.close();
        inputStream.close();

        return pdfOutput;
    }

    public static File convertXlsxToPdf(File excelInput) throws IOException, DocumentException {
        FileInputStream inputStream = new FileInputStream(excelInput);
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        File pdfOutput = new File(excelInput.getParent(), getOutputFileName(excelInput.getName()));
        FileOutputStream outputStream = new FileOutputStream(pdfOutput);

        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            XSSFSheet sheet = workbook.getSheetAt(i);
            PdfPTable table = new PdfPTable(getMaxColumns(sheet));

            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row != null) {
                    for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
                        XSSFCell cell = row.getCell(colNum);
                        if (cell != null) {
                            table.addCell(cell.toString());
                        }
                    }
                }
            }

            document.add(table);
        }

        document.close();
        workbook.close();
        outputStream.close();
        inputStream.close();

        return pdfOutput;
    }

    private static int getMaxColumns(XSSFSheet sheet) {
        int maxColumns = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            XSSFRow row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }
        return maxColumns;
    }

    private static int getMaxColumns(Sheet sheet) {
        int maxColumns = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && row.getLastCellNum() > maxColumns) {
                maxColumns = row.getLastCellNum();
            }
        }
        return maxColumns;
    }

    private static String getOutputFileName(String inputFileName) {
        int dotIndex = inputFileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return inputFileName.substring(0, dotIndex) + ".pdf";
        } else {
            return inputFileName + ".pdf";
        }
    }
}
package com.flowiee.dms.utils;

import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.model.FileExtension;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public static String rootPath = "src/main/resources/static";
    public static String fileUploadPath = rootPath + "/uploads/";
    public static String fileDownloadPath = rootPath + "/downloads/";
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

    public static boolean isAllowUpload(String fileExtension) {
        if (ObjectUtils.isNotEmpty(fileExtension)) {
            for (FileExtension ext : FileExtension.values()) {
                if (ext.key().equalsIgnoreCase(fileExtension) && ext.isAllowUpload()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static File getFileUploaded(FileStorage fileModel) {
        Path path = Paths.get(rootPath + "/" + fileModel.getDirectoryPath() + "/" + fileModel.getStorageName());
        return new File(path.toUri());
    }

    public static Path getTemplateExportTempPath() {
        return Paths.get(excelTemplatePath + "/temp");
    }

    public static Path getDownloadStorageTempPath() {
        return Path.of(fileDownloadPath + "/storage/temp" );
    }

    public static boolean lockFile(File file) {
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
             FileChannel channel = raf.getChannel();
             FileLock lock = channel.lock()) {

            // File đã được khóa thành công
            System.out.println("File đã được khóa thành công: " + file.getAbsolutePath());
            return true;

        } catch (IOException e) {
            // Không thể khóa file, có thể file đang được sử dụng hoặc lỗi khác xảy ra
            System.out.println("Không thể khóa file, có thể file đang được sử dụng hoặc có lỗi khác: " + file.getAbsolutePath());
        }

        return false;
    }

    public static void addFileToDirectory(String filePath, String directoryPath) {
        Path sourcePath = Paths.get(filePath);
        Path targetPath = Paths.get(directoryPath, sourcePath.getFileName().toString());
        try {
            if (!Files.exists(sourcePath) || !Files.exists(targetPath)) {
                return;
            }
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File " + filePath + " copied to " + directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipDirectory(String sourceFolderPath, String zipFilePath) {
        Path sourcePath = Paths.get(sourceFolderPath);
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipFilePath)))) {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(file).toString().replace("\\", "/"));
                    zos.putNextEntry(zipEntry);
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (!sourcePath.equals(dir)) {
                        ZipEntry zipEntry = new ZipEntry(sourcePath.relativize(dir).toString().replace("\\", "/") + "/");
                        zos.putNextEntry(zipEntry);
                        zos.closeEntry();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Directory successfully compressed to " + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
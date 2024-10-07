package com.flowiee.dms.utils;

import com.flowiee.dms.base.StartUp;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.model.FileExtension;
import com.flowiee.dms.model.FolderTree;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileUtils {
    public static String templatePath = "src/main/resources/static";
    public static String fileUploadPath = StartUp.getResourceUploadPath() + File.separator + "uploads" + File.separator;
    public static String fileDownloadPath = StartUp.getResourceUploadPath() + File.separator + "downloads" + File.separator;
    public static String initCsvDataPath = templatePath + File.separator + "data/csv";
    public static String reportTemplatePath = templatePath + File.separator + "report";
    public static String excelTemplatePath = templatePath + File.separator + "templates/excel";
    public static Path logoPath = Paths.get(StartUp.getResourceUploadPath() + File.separator + "dist/img/FlowieeLogo.png");

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

    public static boolean isAllowUpload(String fileExtension, boolean throwException, String message) {
        if (ObjectUtils.isNotEmpty(fileExtension)) {
            for (FileExtension ext : FileExtension.values()) {
                if (ext.key().equalsIgnoreCase(fileExtension) && ext.isAllowUpload()) {
                    return true;
                }
            }
        }
        if (throwException) {
            throw new AppException(String.format(message != null ? message : "File có định dạng .%s chưa được hỗ trợ!", fileExtension));
        }
        return false;
    }

    public static File getFileUploaded(FileStorage fileModel) {
        Path path = Paths.get(StartUp.getResourceUploadPath() + File.separator + fileModel.getDirectoryPath() + File.separator + fileModel.getStorageName());
        return new File(path.toUri());
    }

    public static Path getTemplateExportTempPath() {
        return Paths.get(excelTemplatePath + "/temp");
    }

    public static Path getDownloadStorageTempPath() {
        return Path.of(fileDownloadPath + "/storage/temp" );
    }

    public static Path getImportStorageTempPath() {
        return Path.of(fileUploadPath + "/temp");
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

    public static void addFileToDirectory(String filePath, String directoryPath, StandardCopyOption copyOption) {
        Path sourcePath = Paths.get(filePath);
        Path targetPath = Paths.get(directoryPath, sourcePath.getFileName().toString());
        try {
            if (!Files.exists(sourcePath) || !Files.exists(targetPath)) {
                return;
            }
            Files.copy(sourcePath, targetPath, copyOption);
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
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
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

    // Hàm đệ quy để xây dựng cây thư mục
    public static FolderTree buildFolderTree(File folder, int level, int parentId, String parentName) {
        FolderTree folderTree = FolderTree.builder()
                .name(folder.getName())
                .isDirectory(folder.isDirectory())
                .level(level)
                .parentId(parentId)
                .parentName(parentName)
                .subFiles(new ArrayList<>()).build();

        // Lấy danh sách tất cả các file và thư mục con
        File[] subFiles = folder.listFiles();
        if (subFiles != null) {
            for (File file : subFiles) {
                if (file.isDirectory()) {
                    // Gọi đệ quy cho thư mục con
                    folderTree.getSubFiles().add(buildFolderTree(file, level + 1, parentId, folder.getName()));
                } else {
                    isAllowUpload(CommonUtils.getFileExtension(file.getName()), true, "Tồn tại tệp có định dạng .%s chưa được hỗ trợ!");
                    // Thêm file vào danh sách subFiles (ở đây chỉ thêm file, không gọi đệ quy)
                    folderTree.getSubFiles().add(FolderTree.builder()
                            .name(file.getName())
                            .isDirectory(file.isDirectory())
                            .level(level + 1)
                            .parentId(parentId)
                            .parentName(folder.getName())
                            .file(file)
                            .build());
                }
            }
        }
        return folderTree;
    }

    public static File unzipDirectory(File pFileZip, String pDestDir) throws IOException {
        String lvDestDir = pDestDir;
        if (lvDestDir == null) {
            String pathNotIncludeFileExtension = removeFileExtension(pFileZip.getAbsolutePath());
            lvDestDir = pathNotIncludeFileExtension.substring(0, pathNotIncludeFileExtension.lastIndexOf('\\'));
        }
        File destDirectory = new File(lvDestDir);

        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(pFileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                File newFile = newFile(destDirectory, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new IOException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    File parent = newFile.getParentFile();
                    if (!parent.isDirectory() && !parent.mkdirs()) {
                        throw new IOException("Failed to create directory " + parent);
                    }

                    // write file content
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (pDestDir == null) {
            return new File(removeFileExtension(pFileZip.getAbsolutePath()));
        } else {
            return destDirectory;
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

    public static String removeFileExtension(String originalFilename) {
        String fileExtension = FileUtils.getFileExtension(originalFilename);
        return originalFilename.replaceAll("." + fileExtension, "");
    }

    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        // Đọc dữ liệu từ file
        FileInputStream inputStream = new FileInputStream(file);

        // Tạo đối tượng MockMultipartFile từ File
        MultipartFile multipartFile = new MockMultipartFile(
                file.getName(), // Tên của file
                file.getName(), // Tên gốc của file
                Files.probeContentType(file.toPath()), // "application/octet-stream", // Loại MIME (hoặc có thể là bất kỳ loại nào bạn muốn)
                inputStream); // Dữ liệu của file

        return multipartFile;
    }

    public static String getFileUploadPath() {
        if (StartUp.getResourceUploadPath() == null) {
            throw new AppException("The uploaded file saving directory is not configured, please try again later!");
        }
        return StartUp.getResourceUploadPath() + "/uploads/";
    }

    public static long getFolderSize(File folder) {
        long length = 0;

        // Lấy danh sách tất cả các file và thư mục con
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Nếu là file, lấy dung lượng của file
                    length += file.length();
                } else {
                    // Nếu là thư mục, gọi đệ quy để lấy dung lượng thư mục con
                    length += getFolderSize(file);
                }
            }
        }

        return length;
    }
}
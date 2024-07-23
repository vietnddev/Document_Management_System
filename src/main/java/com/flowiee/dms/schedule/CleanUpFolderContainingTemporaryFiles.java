package com.flowiee.dms.schedule;

import com.flowiee.dms.utils.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@Component
public class CleanUpFolderContainingTemporaryFiles {

    @Scheduled(cron = "0 0/1 * * * ?")
    public void cleanDownloadFolderTemp() {
        File folderTemp = FileUtils.getDownloadStorageTempPath().toFile();
        if (folderTemp != null) {
            for (File file : folderTemp.listFiles()) {
                if (!file.exists()) {
                    System.out.println("File không tồn tại: " + file.getAbsolutePath());
                    continue;
                }
                // Kiểm tra quyền truy cập đọc và ghi
                try (RandomAccessFile raf = new RandomAccessFile(file, "rw");
                     FileChannel channel = raf.getChannel();
                     FileLock lock = channel.tryLock()) {
                    // Nếu có thể khóa file, nó không được sử dụng bởi tiến trình khác
                    if (lock != null) {
                        if (file.delete()) {
                            System.out.println("CleanUpFolderContainingTemporaryFiles - Xoa thanh cong file: " + file.getAbsolutePath());
                        } else {
                            System.out.println("CleanUpFolderContainingTemporaryFiles - Xoa that bai file: " + file.getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    // File đang được sử dụng hoặc có lỗi khác xảy ra
                    System.out.println("Không thể truy cập file, có thể file đang được sử dụng: " + file.getAbsolutePath());
                }
            }
        }
    }
}
package com.flowiee.dms.schedule;

import com.flowiee.dms.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CleanUpFolderContainingTemporaryFiles {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Scheduled(cron = "0 0/1 * * * ?")
    public void cleanDownloadFolderTemp() {
        File folderTemp = FileUtils.getDownloadStorageTempPath().toFile();
        if (folderTemp != null) {
            for (File file : folderTemp.listFiles()) {
                if (!file.exists()) {
                    System.out.println("File does not exists: " + file.getAbsolutePath());
                    continue;
                }
                String message = "Delete fail";
                if (file.delete()) {
                    message = "Delete success";
                }
                logger.info(String.format("Job %s - %s file: '%s'", this.getClass().getName(), message, file.getAbsolutePath()));
            }
        }
    }
}
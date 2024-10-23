package com.flowiee.dms.schedule;

import com.flowiee.dms.entity.system.ScheduleStatus;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.ScheduleTask;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class CleanUpFolderDownloadTempScheduleExecutor extends ScheduleService {
    private int pendingTimeDelete = 10;//minutes

    @Scheduled(cron = "0 0/10 * * * ?")
    @Override
    public void execute() {
        ScheduleStatus scheduleStatus = startSchedule(ScheduleTask.CleanUpFolderDownloadTemp);
        try {
            File folderTemp = FileUtils.getDownloadStorageTempPath().toFile();
            if (folderTemp != null) {
                for (File file : folderTemp.listFiles()) {
                    if (!file.exists()) {
                        System.out.println("File does not exists: " + file.getAbsolutePath());
                        continue;
                    }
                    if (canDelete(file)) {
                        String message = file.delete() ? "Delete success" : "Delete fail";
                        logger.info(String.format("Job %s - %s file: '%s'", ScheduleTask.CleanUpFolderDownloadTemp, message, file.getAbsolutePath()));
                    }
                }
            }
        } catch (AppException | IOException ex) {
            logger.info(String.format("An error occurred while processing schedule %s", ScheduleTask.CleanUpFolderDownloadTemp), ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
    }

    private boolean canDelete(File file) throws IOException {
        BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        FileTime lastAccessTime = attrs.lastAccessTime();
        Date lastAccessDate = new Date(lastAccessTime.toMillis());
        Date now = new Date();
        long diffInMillis = now.getTime() - lastAccessDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis);
        return diffInMinutes >= pendingTimeDelete;
    }
}
package com.flowiee.dms.schedule;

import com.flowiee.dms.entity.system.ScheduleStatus;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.ScheduleTask;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CleanUpFolderDownloadTempScheduleExecutor extends ScheduleService {

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
                    String message = "Delete fail";
                    if (file.delete()) {
                        message = "Delete success";
                    }
                    logger.info(String.format("Job %s - %s file: '%s'", ScheduleTask.CleanUpFolderDownloadTemp, message, file.getAbsolutePath()));
                }
            }
        } catch (AppException ex) {
            logger.info(String.format("An error occurred while processing schedule %s", ScheduleTask.CleanUpFolderDownloadTemp), ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
    }
}
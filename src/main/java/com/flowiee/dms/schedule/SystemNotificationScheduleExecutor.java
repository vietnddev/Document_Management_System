package com.flowiee.dms.schedule;

import com.flowiee.dms.base.StartUp;
import com.flowiee.dms.entity.system.ScheduleStatus;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.repository.storage.FileStorageRepository;
import com.flowiee.dms.service.system.SendMailService;
import com.flowiee.dms.utils.constants.ConfigCode;
import com.flowiee.dms.utils.constants.ScheduleTask;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class SystemNotificationScheduleExecutor extends ScheduleService {
    FileStorageRepository fileStorageRepository;
    SendMailService       sendMailService;

    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void execute() {
        ScheduleStatus scheduleStatus = startSchedule(ScheduleTask.SystemNotification);
        int usageWarningPercent = 80;
        String subjectWR = "Flowiee System notification";
        String emailReceiveWR = "nguyenducviet.vietnd@gmail.com";
        String contentWR = "The storage limit of the Flowiee system was exceeded!";
        try {
            long limitOfSystem = Long.parseLong(StartUp.getSystemConfig(ConfigCode.storageLimitAllUser).getValue());//default is GB unit
            long limitOfSystemBytes = limitOfSystem * 1024 * 1024 * 1024;
            long memoryUsed = fileStorageRepository.getCurrentStorageUsage(null);//Bytes
            float percentUsed = memoryUsed / limitOfSystemBytes * 100;
            if (percentUsed >= usageWarningPercent) {
                sendMailService.sendMail(subjectWR, emailReceiveWR, contentWR);
            }
        } catch (AppException | UnsupportedEncodingException | MessagingException ex) {
            logger.info(String.format("An error occurred while processing schedule %s", ScheduleTask.CleanUpRecycleBin), ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
    }
}
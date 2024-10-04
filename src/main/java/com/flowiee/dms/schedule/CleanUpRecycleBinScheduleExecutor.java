package com.flowiee.dms.schedule;

import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.system.ScheduleStatus;
import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.repository.system.SystemConfigRepository;
import com.flowiee.dms.service.storage.DocActionService;
import com.flowiee.dms.utils.constants.ConfigCode;
import com.flowiee.dms.utils.constants.ScheduleTask;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class CleanUpRecycleBinScheduleExecutor extends ScheduleService {
    DocumentRepository documentRepository;
    DocActionService docActionService;
    SystemConfigRepository systemConfigRepository;

    @Scheduled(cron = "0 0 1 * * ?")
    @Override
    public void execute() {
        ScheduleStatus scheduleStatus = startSchedule(ScheduleTask.CleanUpRecycleBin);
        try {
            SystemConfig systemConfig = systemConfigRepository.findByCode(ConfigCode.timeStorageFileInRecycleBin.name());
            if (systemConfig != null && systemConfig.getValue() != null) {
                int timeCleanUpRecycleBin = Integer.parseInt(systemConfig.getValue());
                List<Document> expiredDocuments = documentRepository.findExpiredDocumentsInRecycleBin(LocalDateTime.now().minusDays(timeCleanUpRecycleBin));
                for (Document expiredDoc : expiredDocuments) {
                    docActionService.deleteDoc(expiredDoc.getId(), true, true, DocActionService.DELETE_SCHEDULE);
                }
            }
        } catch (AppException ex) {
            logger.info(String.format("An error occurred while processing schedule %s", ScheduleTask.CleanUpRecycleBin), ex);
            scheduleStatus.setErrorMsg(ex.getMessage());
        } finally {
            endSchedule(scheduleStatus);
        }
    }
}
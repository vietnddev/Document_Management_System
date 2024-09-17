package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.SystemLog;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.repository.system.SystemLogRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.SystemLogService;
import com.flowiee.dms.utils.ChangeLog;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.constants.LogType;
import com.flowiee.dms.utils.constants.MasterObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SystemLogServiceImpl extends BaseService implements SystemLogService {
    SystemLogRepository systemLogRepository;

    @Override
    public Page<SystemLog> findAll(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("id").descending());
        Page<SystemLog> logs = systemLogRepository.findAll(pageable);
        for (SystemLog systemLog : logs.getContent()) {
            if (systemLog.getAccount() != null) {
                systemLog.setAccountName(systemLog.getAccount().getFullName());
            }
        }
        return logs;
    }

    @Override
    public SystemLog writeLogCreate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.I, title, content, "-");
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, ChangeLog changeLog) {
        return this.writeLog(module, function, object, LogType.U, title, changeLog.getOldValues(), changeLog.getNewValues());
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.U, title, content, "-");
    }

    @Override
    public SystemLog writeLogUpdate(MODULE module, ACTION function, MasterObject object, String title, String content, String contentChange) {
        return this.writeLog(module, function, object, LogType.U, title, content, contentChange);
    }

    @Override
    public SystemLog writeLogDelete(MODULE module, ACTION function, MasterObject object, String title, String content) {
        return this.writeLog(module, function, object, LogType.D, title, content, "-");
    }

    @Override
    public SystemLog writeLog(MODULE module, ACTION function, MasterObject object, LogType mode, String title, String content, String contentChange) {
        SystemLog systemLog = new SystemLog();
        systemLog.setModule(module.name());
        systemLog.setFunction(function.name());
        systemLog.setObject(object.name());
        systemLog.setMode(mode.name());
        systemLog.setTitle(title);
        systemLog.setContent(content);
        systemLog.setContentChange(contentChange);
        systemLog.setIp(CommonUtils.getUserPrincipal().getIp());
        systemLog.setAccount(new Account(CommonUtils.getUserPrincipal().getId()));
        systemLog.setCreatedBy(CommonUtils.getUserPrincipal().getId());
        return systemLogRepository.save(systemLog);
    }
}
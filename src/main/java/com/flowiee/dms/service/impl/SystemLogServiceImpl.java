package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.SystemLog;
import com.flowiee.dms.repository.SystemLogRepository;
import com.flowiee.dms.service.SystemLogService;
import com.flowiee.dms.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class SystemLogServiceImpl implements SystemLogService {
    @Autowired private SystemLogRepository systemLogRepo;
    @Autowired private EntityManager       entityManager;

    @Override
    public Page<SystemLog> findAll(int pageSize, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").descending());
        return systemLogRepo.findAll(pageable);
    }

    @Override
    public List<SystemLog> getAll() {
        List<SystemLog> dataReturn = new ArrayList<>();
        String srtSQL = "select l.id, l.module, l.action, l.noi_dung, l.noi_dung_cap_nhat, l.ip, a.username from sys_log l inner join sys_account a on a.id = l.created_by";
        Query result = entityManager.createNativeQuery(srtSQL);
        List<Object[]> listData = result.getResultList();
        for (Object[] data : listData) {
            SystemLog systemLog = new SystemLog();
            systemLog.setId(Integer.parseInt(String.valueOf(data[0])));
            systemLog.setModule(String.valueOf(data[1]));
            systemLog.setAction(String.valueOf(data[2]));
            systemLog.setContent(String.valueOf(data[3]));
            systemLog.setContentChange(String.valueOf(data[4]));
            systemLog.setIp(String.valueOf(data[5]));
            systemLog.setUsername(String.valueOf(data[6]));
            dataReturn.add(systemLog);
        }
        return dataReturn;
    }

    @Override
    public SystemLog writeLog(SystemLog log) {
        return systemLogRepo.save(log);
    }

    @Override
    public SystemLog writeLog(String module, String action, String content) {
        SystemLog systemLog = new SystemLog();
        systemLog.setModule(module);
        systemLog.setAction(action);
        systemLog.setContent(content);
        systemLog.setContentChange(null);
        systemLog.setCreatedBy(CommonUtils.getCurrentAccountId());
        systemLog.setIp(CommonUtils.getCurrentAccountIp());
        return systemLogRepo.save(systemLog);
    }

    @Override
    public SystemLog writeLog(String module, String action, String content, String contentChange) {
        SystemLog systemLog = new SystemLog();
        systemLog.setModule(module);
        systemLog.setAction(action);
        systemLog.setContent(content);
        systemLog.setContentChange(contentChange);
        systemLog.setCreatedBy(CommonUtils.getCurrentAccountId());
        systemLog.setIp(CommonUtils.getCurrentAccountIp());
        return systemLogRepo.save(systemLog);
    }
}
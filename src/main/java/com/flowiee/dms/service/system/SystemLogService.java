package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.system.SystemLog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SystemLogService extends BaseService<SystemLog> {
    Page<SystemLog> findAll(int pageSize, int pageNum);

    List<SystemLog> getAll();

    SystemLog writeLog(SystemLog log);

    SystemLog writeLog(String module, String action, String content, String contentChange);
}
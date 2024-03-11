package com.flowiee.dms.service;

import com.flowiee.dms.entity.SystemLog;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SystemLogService {
    Page<SystemLog> findAll(int pageSize, int pageNum);

    List<SystemLog> getAll();

    SystemLog writeLog(SystemLog log);

    SystemLog writeLog(String module, String action, String content);

    SystemLog writeLog(String module, String action, String content, String contentChange);
}
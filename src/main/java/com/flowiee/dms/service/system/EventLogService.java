package com.flowiee.dms.service.system;

import com.flowiee.dms.entity.system.EventLog;
import org.aspectj.lang.JoinPoint;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

public interface EventLogService {
    EventLog writeLog(ServletRequestAttributes pServletRequestAttributes, JoinPoint pJoinPoint, LocalDateTime pCreateTime, String pApplication);
}
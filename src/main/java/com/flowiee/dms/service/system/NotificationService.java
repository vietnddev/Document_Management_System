package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.system.Notification;

import java.util.List;

public interface NotificationService extends BaseService<Notification> {
    List<Notification> findByReceive(Integer receivedId);
}
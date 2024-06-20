package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.system.Notification;

import java.util.List;

public interface NotificationService extends BaseCurdService<Notification> {
    List<Notification> findByReceive(Integer receivedId);
}
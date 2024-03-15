package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.Notification;

import java.util.List;

public interface NotificationService extends BaseService<Notification> {
    List<Notification> findByReceive(Integer receivedId);
}
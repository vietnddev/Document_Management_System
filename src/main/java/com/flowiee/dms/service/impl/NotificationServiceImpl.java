package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.Notification;
import com.flowiee.dms.repository.NotificationRepository;
import com.flowiee.dms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired private NotificationRepository notificationRepo;

    @Override
    public Notification findById(Integer entityId) {
        return notificationRepo.findById(entityId).orElse(null);
    }

    @Override
    public Notification save(Notification entity) {
        return notificationRepo.save(entity);
    }

    @Override
    public Notification update(Notification entity, Integer entityId) {
        return null;
    }

    @Override
    public String delete(Integer entityId) {
        return null;
    }

    @Override
    public List<Notification> findByReceive(Integer receivedId) {
        return findByReceive(receivedId);
    }
}
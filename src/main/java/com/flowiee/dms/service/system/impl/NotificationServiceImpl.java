package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.repository.system.NotificationRepository;
import com.flowiee.dms.service.system.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired private NotificationRepository notificationRepo;

    @Override
    public Optional<Notification> findById(Integer entityId) {
        return notificationRepo.findById(entityId);
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
package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.repository.system.NotificationRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.NotificationService;
import com.flowiee.dms.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationServiceImpl extends BaseService implements NotificationService {
    NotificationRepository notificationRepository;

    @Override
    public Optional<Notification> findById(Integer entityId) {
        return notificationRepository.findById(entityId);
    }

    @Override
    public Notification save(Notification notify) {
        notify.setRead(false);
        return notificationRepository.save(notify);
    }

    @Override
    public Notification update(Notification entity, Integer entityId) {
        return null;
    }

    @Override
    public String delete(Integer notifyId) {
        notificationRepository.deleteById(notifyId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<Notification> findByReceive(Integer receivedId) {
        return findByReceive(receivedId);
    }
}
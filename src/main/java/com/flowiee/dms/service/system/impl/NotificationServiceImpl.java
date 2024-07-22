package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.repository.system.NotificationRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Notification> findByReceive(int pageNum, int pageSize, Integer receivedId) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by("createdAt").ascending());
        if (pageNum == -1 || pageSize == -1) {
            pageable = Pageable.unpaged();
        }
        return notificationRepository.findByReceiver(receivedId, pageable);
    }
}
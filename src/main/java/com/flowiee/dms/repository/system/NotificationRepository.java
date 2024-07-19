package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("from Notification n where n.receiver=:accountId")
    List<Notification> findByTo();
}
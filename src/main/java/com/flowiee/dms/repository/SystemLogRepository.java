package com.flowiee.dms.repository;

import com.flowiee.dms.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Integer> {
}
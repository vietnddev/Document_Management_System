package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.ScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleStatusRepository extends JpaRepository<ScheduleStatus, Integer> {
}
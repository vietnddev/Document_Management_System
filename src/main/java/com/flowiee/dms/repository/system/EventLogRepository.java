package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    @Query("from EventLog el " +
            "where 1=1 " +
            "and (:requestId is null or el.requestId = :requestId) " +
            "and (:httpMethod is null or el.httpMethod = :httpMethod) " +
            "and (:httpMethod is null or el.httpMethod = :httpMethod) " +
            "and (:requestUrl is null or el.requestUrl = :requestUrl) " +
            "and (:startTime is null or el.createdTime = :startTime)")
    EventLog find(@Param("requestId") Long requestId,
                  @Param("httpMethod") String httpMethod,
                  @Param("requestUrl") String requestUrl,
                  @Param("startTime") LocalDateTime startTime);
}
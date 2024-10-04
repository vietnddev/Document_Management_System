package com.flowiee.dms.repository.system;

import com.flowiee.dms.entity.system.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Integer> {
    @Query("from SystemConfig order by sort")
    List<SystemConfig> findAll();

    SystemConfig findByCode(String code);
}
package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.system.SystemConfig;

import java.util.List;

public interface ConfigService extends BaseService<SystemConfig> {
    List<SystemConfig> findAll();

    String refreshApp();
}
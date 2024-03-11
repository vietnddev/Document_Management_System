package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.SystemConfig;

import java.util.List;

public interface ConfigService extends BaseService<SystemConfig> {
    List<SystemConfig> findAll();

    String refreshApp();
}
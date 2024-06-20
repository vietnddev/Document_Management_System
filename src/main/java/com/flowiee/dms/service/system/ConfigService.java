package com.flowiee.dms.service.system;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.system.SystemConfig;

import java.util.List;

public interface ConfigService extends BaseCurdService<SystemConfig> {
    List<SystemConfig> findAll();

    String refreshApp();
}
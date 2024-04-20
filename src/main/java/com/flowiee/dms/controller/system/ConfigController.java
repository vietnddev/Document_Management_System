package com.flowiee.dms.controller.system;

import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.NotFoundException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.service.system.ConfigService;
import com.flowiee.dms.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "Config", description = "Quản lý cấu hình hệ thống")
public class ConfigController {
    @Autowired
    private ConfigService configService;

    @Operation(summary = "Find all configs")
    @GetMapping("/config/all")
    @PreAuthorize("@vldModuleSystem.setupConfig(true)")
    public ApiResponse<List<SystemConfig>> findConfigs() {
        try {
            return ApiResponse.ok(configService.findAll());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "configs"), ex);
        }
    }

    @Operation(summary = "Update config")
    @PutMapping("/config/update/{id}")
    @PreAuthorize("@vldModuleSystem.setupConfig(true)")
    public ApiResponse<SystemConfig> updateConfig(@RequestBody SystemConfig config, @PathVariable("id") Integer configId) {
        try {
            if (configId <= 0 || configService.findById(configId).isEmpty()) {
                throw new NotFoundException("Config not found!");
            }
            return ApiResponse.ok(configService.update(config, configId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.UPDATE_ERROR_OCCURRED, config), ex);
        }
    }
}
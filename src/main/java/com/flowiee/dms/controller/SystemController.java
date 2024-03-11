package com.flowiee.dms.controller;

import com.flowiee.dms.core.BaseController;
import com.flowiee.dms.entity.SystemConfig;
import com.flowiee.dms.entity.SystemLog;
import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.core.exception.NotFoundException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.service.ConfigService;
import com.flowiee.dms.service.SystemLogService;
import com.flowiee.dms.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "System API", description = "Quản lý hệ thống")
public class SystemController extends BaseController {
    @Autowired private SystemLogService logService;
    @Autowired private ConfigService configService;

    @Operation(summary = "Find all log")
    @GetMapping("/log/all")
    public ApiResponse<List<SystemLog>> findLogs(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            if (!super.vldModuleSystem.readLog(true)) {
                return null;
            }
            Page<SystemLog> logPage = logService.findAll(pageSize, pageNum - 1);
            return ApiResponse.ok(logPage.getContent(), pageNum, pageSize, logPage.getTotalPages(), logPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "system log"));
        }
    }

    @Operation(summary = "Find all configs")
    @GetMapping("/config/all")
    public ApiResponse<List<SystemConfig>> findConfigs() {
        try {
            if (!super.vldModuleSystem.setupConfig(true)) {
                return null;
            }
            return ApiResponse.ok(configService.findAll());
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "configs"), ex);
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "configs"));
        }
    }

    @Operation(summary = "Update config")
    @PutMapping("/config/update/{id}")
    public ApiResponse<SystemConfig> updateConfig(@RequestBody SystemConfig config, @PathVariable("id") Integer configId) {
        try {
            if (!super.vldModuleSystem.setupConfig(true)) {
                return null;
            }
            if (configId <= 0 || configService.findById(configId) == null) {
                throw new NotFoundException("Config not found!");
            }
            return ApiResponse.ok(configService.update(config, configId));
        } catch (RuntimeException ex) {
            logger.error(String.format(MessageUtils.UPDATE_ERROR_OCCURRED, config));
            throw new AppException(String.format(MessageUtils.UPDATE_ERROR_OCCURRED, config));
        }
    }

    @GetMapping("/refresh")
    public ApiResponse<String> refreshApp() {
        return ApiResponse.ok(configService.refreshApp());
    }
}
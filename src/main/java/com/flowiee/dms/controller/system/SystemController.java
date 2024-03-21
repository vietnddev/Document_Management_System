package com.flowiee.dms.controller.system;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.entity.system.SystemLog;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.NotFoundException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.service.system.ConfigService;
import com.flowiee.dms.service.system.SystemLogService;
import com.flowiee.dms.utils.MessageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/sys")
@Tag(name = "System API", description = "Quản lý hệ thống")
public class SystemController extends BaseController {
    @Autowired
    private SystemLogService logService;
    @Autowired
    private ConfigService configService;

    @Operation(summary = "Find all log")
    @GetMapping("/log/all")
    @PreAuthorize("@vldModuleSystem.readLog(true)")
    public ApiResponse<List<SystemLog>> findLogs(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            Page<SystemLog> logPage = logService.findAll(pageSize, pageNum - 1);
            return ApiResponse.ok(logPage.getContent(), pageNum, pageSize, logPage.getTotalPages(), logPage.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(MessageUtils.SEARCH_ERROR_OCCURRED, "system log"), ex);
        }
    }

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

    @GetMapping("/refresh")
    public ApiResponse<String> refreshApp() {
        return ApiResponse.ok(configService.refreshApp());
    }
}
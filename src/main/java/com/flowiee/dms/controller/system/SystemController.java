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
    private ConfigService configService;

    @GetMapping("/refresh")
    public ApiResponse<String> refreshApp() {
        return ApiResponse.ok(configService.refreshApp());
    }
}
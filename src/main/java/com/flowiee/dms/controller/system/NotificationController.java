package com.flowiee.dms.controller.system;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.entity.system.Notification;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.service.system.NotificationService;
import com.flowiee.dms.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${app.api.prefix}/system/notify")
@Tag(name = "Account system API", description = "Quản lý tài khoản hệ thống")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class NotificationController extends BaseController {
    NotificationService notificationService;

    @Operation(summary = "Find all notify of an account")
    @GetMapping("/all")
    public ApiResponse<List<Notification>> findAllNotify(@RequestParam("pageSize") Integer pageSize,
                                                         @RequestParam("pageNum") Integer pageNum) {
        Page<Notification> notifyPage = notificationService.findByReceive(pageNum - 1, pageSize, CommonUtils.getUserPrincipal().getId());
        return ApiResponse.ok(notifyPage.getContent(), pageNum, pageSize, notifyPage.getTotalPages(), notifyPage.getTotalElements());
    }
}
package com.flowiee.dms.controller.system;

import com.flowiee.dms.base.BaseController;
import com.flowiee.dms.service.system.GroupAccountService;
import com.flowiee.dms.service.system.RoleService;
import com.flowiee.dms.entity.system.GroupAccount;
import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.ApiResponse;
import com.flowiee.dms.model.role.RoleModel;
import com.flowiee.dms.utils.constants.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${app.api.prefix}/sys/group-account")
@Tag(name = "Group account API", description = "Quản lý nhóm người dùng")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GroupAccountController extends BaseController {
    RoleService         roleService;
    GroupAccountService groupAccountService;

    @Operation(summary = "Find all group account")
    @GetMapping("/all")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ApiResponse<List<GroupAccount>> findAll(@RequestParam("pageSize") int pageSize, @RequestParam("pageNum") int pageNum) {
        try {
            Page<GroupAccount> groupAccounts = groupAccountService.findAll(pageSize, pageNum - 1);
            return ApiResponse.ok(groupAccounts.getContent(), pageNum, pageSize, groupAccounts.getTotalPages(), groupAccounts.getTotalElements());
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "group account"), ex);
        }
    }

    @Operation(summary = "Find detail group")
    @GetMapping("/{groupId}")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ApiResponse<GroupAccount> findDetailAccount(@PathVariable("groupId") Integer groupId) {
        Optional<GroupAccount> groupAcc = groupAccountService.findById(groupId);
        if (groupAcc.isEmpty()) {
            throw new BadRequestException("Group account not found");
        }
        return ApiResponse.ok(groupAcc.get());
    }

    @Operation(summary = "Create group account")
    @PostMapping(value = "/create")
    @PreAuthorize("@vldModuleSystem.insertGroupAccount(true)")
    public ApiResponse<GroupAccount> save(@RequestBody GroupAccount groupAccount) {
        try {
            if (groupAccount == null) {
                throw new BadRequestException("Invalid group account");
            }
            return ApiResponse.ok(groupAccountService.save(groupAccount));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR.getDescription(), "group account"), ex);
        }
    }

    @Operation(summary = "Update group account")
    @PutMapping(value = "/update/{groupId}")
    @PreAuthorize("@vldModuleSystem.updateGroupAccount(true)")
    public ApiResponse<GroupAccount> update(@RequestBody GroupAccount groupAccount, @PathVariable("groupId") Integer groupId) {
        return ApiResponse.ok(groupAccountService.update(groupAccount, groupId));
    }

    @Operation(summary = "Delete group account")
    @DeleteMapping(value = "/delete/{groupId}")
    @PreAuthorize("@vldModuleSystem.deleteGroupAccount(true)")
    public ApiResponse<String> delete(@PathVariable("groupId") Integer groupId) {
        return ApiResponse.ok(groupAccountService.delete(groupId));
    }

    @Operation(summary = "Find rights of group")
    @GetMapping("/{groupId}/rights")
    @PreAuthorize("@vldModuleSystem.readGroupAccount(true)")
    public ApiResponse<List<RoleModel>> findRights(@PathVariable("groupId") Integer groupId) {
        try {
            return ApiResponse.ok(roleService.findAllRoleByGroupId(groupId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.SEARCH_ERROR.getDescription(), "rights of group account"), ex);
        }
    }

    @Operation(summary = "Grant rights to group")
    @PutMapping(value = "/grant-rights/{groupId}")
    @PreAuthorize("@vldModuleSystem.updateGroupAccount(true)")
    public ApiResponse<List<RoleModel>> update(@RequestBody List<RoleModel> rights, @PathVariable("groupId") Integer groupId) {
        try {
            if (groupAccountService.findById(groupId).isEmpty()) {
                throw new BadRequestException("Group not found");
            }
            return ApiResponse.ok(roleService.updateRightsOfGroup(rights, groupId));
        } catch (RuntimeException ex) {
            throw new AppException(String.format(ErrorCode.UPDATE_ERROR.getDescription(), "group account"), ex);
        }
    }
}
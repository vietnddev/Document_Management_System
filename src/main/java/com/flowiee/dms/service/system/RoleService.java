package com.flowiee.dms.service.system;

import com.flowiee.dms.entity.system.AccountRole;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.role.ActionModel;
import com.flowiee.dms.model.role.RoleModel;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleModel> findAllRoleByAccountId(Long accountId);

    List<RoleModel> findAllRoleByGroupId(Long groupId);

    List<ActionModel> findAllAction();

    Optional<AccountRole> findById(Long id);

    List<AccountRole> findByAccountId(Long accountId);

    List<AccountRole> findByGroupId(Long accountId);

    String updatePermission(String moduleKey, String actionKey, Long accountId);

    boolean isAuthorized(long accountId, String module, String action);

    String deleteAllRole(Long groupId, Long accountId);

    List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Long groupId);

    List<AccountRole> findByAction(ACTION action);
}
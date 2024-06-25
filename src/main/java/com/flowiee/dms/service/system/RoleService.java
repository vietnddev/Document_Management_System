package com.flowiee.dms.service.system;

import com.flowiee.dms.entity.system.AccountRole;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.role.ActionModel;
import com.flowiee.dms.model.role.RoleModel;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<RoleModel> findAllRoleByAccountId(Integer accountId);

    List<RoleModel> findAllRoleByGroupId(Integer groupId);

    List<ActionModel> findAllAction();

    Optional<AccountRole> findById(Integer id);

    List<AccountRole> findByAccountId(Integer accountId);

    List<AccountRole> findByGroupId(Integer accountId);

    String updatePermission(String moduleKey, String actionKey, Integer accountId);

    boolean isAuthorized(int accountId, String module, String action);

    String deleteAllRole(Integer groupId, Integer accountId);

    List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Integer groupId);

    List<AccountRole> findByAction(ACTION action);
}
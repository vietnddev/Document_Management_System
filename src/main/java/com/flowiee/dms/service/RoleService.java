package com.flowiee.dms.service;

import com.flowiee.dms.entity.AccountRole;
import com.flowiee.dms.model.role.ActionModel;
import com.flowiee.dms.model.role.FlowieeRole;

import java.util.List;

public interface RoleService {
    List<FlowieeRole> findAllRoleByAccountId(Integer accountId);

    List<ActionModel> findAllAction();

    AccountRole findById(Integer id);

    List<AccountRole> findByAccountId(Integer accountId);

    String updatePermission(String moduleKey, String actionKey, Integer accountId);

    boolean isAuthorized(int accountId, String module, String action);

    String deleteAllRole(Integer accountId);
}
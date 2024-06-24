package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.system.AccountRole;
import com.flowiee.dms.entity.system.GroupAccount;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.model.role.ActionModel;
import com.flowiee.dms.model.role.RoleModel;
import com.flowiee.dms.model.role.ModuleModel;
import com.flowiee.dms.repository.system.AccountRoleRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.system.GroupAccountService;
import com.flowiee.dms.service.system.RoleService;
import com.flowiee.dms.utils.MessageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRoleServiceImpl extends BaseService implements RoleService {
    AccountService        accountService;
    GroupAccountService   groupAccountService;
    AccountRoleRepository accountRoleRepository;

    public AccountRoleServiceImpl(AccountRoleRepository accountRoleRepo, @Lazy AccountService accountService, GroupAccountService groupAccountService) {
        this.accountRoleRepository = accountRoleRepo;
        this.accountService = accountService;
        this.groupAccountService = groupAccountService;
    }

    @Override
    public List<RoleModel> findAllRoleByAccountId(Integer accountId) {
        Optional<Account> account = accountService.findById(accountId);
        if (account.isEmpty()) {
            return new ArrayList<>();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRole(null, accountId, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<RoleModel> findAllRoleByGroupId(Integer groupId) {
        Optional<GroupAccount> groupAcc = groupAccountService.findById(groupId);
        if (groupAcc.isEmpty()) {
            return List.of();
        }
        List<RoleModel> listReturn = new ArrayList<>();
        for (ACTION act : ACTION.values()) {
            listReturn.add(initRole(groupId, null, act.getModuleKey(), act.getModuleLabel(), act.name(), act.getActionLabel()));
        }
        return listReturn;
    }

    @Override
    public List<ActionModel> findAllAction() {
        List<ActionModel> listAction = new ArrayList<>();
        for (ACTION sysAction : ACTION.values()) {
            listAction.add(new ActionModel(sysAction.getActionKey(), sysAction.getActionLabel(), sysAction.getModuleKey()));
        }
        return listAction;
    }

    @Override
    public Optional<AccountRole> findById(Integer id) {
        return accountRoleRepository.findById(id);
    }

    @Override
    public List<AccountRole> findByAccountId(Integer accountId) {
        return accountRoleRepository.findByAccountId(accountId);
    }

    @Override
    public List<AccountRole> findByGroupId(Integer accountId) {
        return accountRoleRepository.findByGroupId(accountId);
    }

    @Override
    public String updatePermission(String moduleKey, String actionKey, Integer accountId) {
        accountRoleRepository.save(new AccountRole(moduleKey, actionKey, accountId, null));
        return MessageUtils.UPDATE_SUCCESS;
    }

    @Override
    public boolean isAuthorized(int accountId, String module, String action) {
        return accountRoleRepository.isAuthorized(null, accountId, module, action) != null;
    }

    @Override
    public String deleteAllRole(Integer groupId, Integer accountId) {
        if (groupId == null && accountId == null) {
            throw new IllegalArgumentException("groupId and accountId cannot be null");
        }
        accountRoleRepository.deleteByAccountId(accountId);
        return MessageUtils.DELETE_SUCCESS;
    }

    @Override
    public List<RoleModel> updateRightsOfGroup(List<RoleModel> rights, Integer groupId) {
        this.deleteAllRole(groupId, null);
        List<RoleModel> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(rights)) {
            return list;
        }
        for (RoleModel role : rights) {
            if (ObjectUtils.isEmpty(role)) {
                return list;
            }
            if (role.getIsAuthor() == null || !role.getIsAuthor()) {
                continue;
            }
            String moduleKey = role.getModule().getModuleKey();
            String actionKey = role.getAction().getActionKey();
            accountRoleRepository.save(new AccountRole(moduleKey, actionKey, null, groupId));
            list.add(role);
        }
        return list;
    }

    @Override
    public List<AccountRole> findByAction(ACTION action) {
        return accountRoleRepository.findByModuleAndAction(action.getModuleKey(), action.getActionKey());
    }

    private RoleModel initRole(Integer pGroupId, Integer pAccountId, String pModuleKey, String pModuleLabel, String pActionKey, String pActionLabel) {
        RoleModel roleModel = new RoleModel();

        ModuleModel module = new ModuleModel();
        module.setModuleKey(pModuleKey);
        module.setModuleLabel(pModuleLabel);
        roleModel.setModule(module);

        ActionModel action = new ActionModel();
        action.setActionKey(pActionKey);
        action.setActionLabel(pActionLabel);
        action.setModuleKey(pModuleKey);
        roleModel.setAction(action);

        AccountRole isAuthor = accountRoleRepository.isAuthorized(pGroupId, pAccountId, pModuleKey, pActionKey);
        roleModel.setIsAuthor(isAuthor != null);

        roleModel.setGroupId(pGroupId);
        roleModel.setAccountId(pAccountId);

        return roleModel;
    }
}
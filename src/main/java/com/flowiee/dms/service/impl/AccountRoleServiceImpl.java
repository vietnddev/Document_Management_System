package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.Account;
import com.flowiee.dms.entity.AccountRole;
import com.flowiee.dms.model.role.ActionModel;
import com.flowiee.dms.model.role.FlowieeRole;
import com.flowiee.dms.model.role.ModuleModel;
import com.flowiee.dms.repository.AccountRoleRepository;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.service.RoleService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountRoleServiceImpl implements RoleService {
    @Autowired private AccountRoleRepository accountRoleRepo;
    @Autowired private AccountService accountService;

    @Override
    public List<FlowieeRole> findAllRoleByAccountId(Integer accountId) {
        Account account = accountService.findById(accountId);
        if (account == null) {
            return null;
        }
        List<FlowieeRole> listReturn = new ArrayList<>();

        for (AppConstants.SYSTEM_MODULE sys : AppConstants.SYSTEM_MODULE.values()) {
            switch (sys) {  
                case PRODUCT:
                    for (AppConstants.PRODUCT_ACTION actions : AppConstants.PRODUCT_ACTION.values()) {
                        listReturn.add(buildFlowieeRole(accountId, sys.name(), sys.getLabel(),actions.name(), actions.getLabel()));
                    }
                    break;
                case STORAGE:
                    for (AppConstants.STORAGE_ACTION actions : AppConstants.STORAGE_ACTION.values()) {
                        listReturn.add(buildFlowieeRole(accountId, sys.name(), sys.getLabel(),actions.name(), actions.getLabel()));
                    }
                    break;
                case SYSTEM:
                    for (AppConstants.SYSTEM_ACTION actions : AppConstants.SYSTEM_ACTION.values()) {
                        listReturn.add(buildFlowieeRole(accountId, sys.name(), sys.getLabel(),actions.name(), actions.getLabel()));
                    }
                    break;
                case CATEGORY:
                    for (AppConstants.CATEGORY_ACTION actions : AppConstants.CATEGORY_ACTION.values()) {
                        listReturn.add(buildFlowieeRole(accountId, sys.name(), sys.getLabel(),actions.name(), actions.getLabel()));
                    }
                    break;
            }
        }
        return listReturn;
    }

    @Override
    public List<ActionModel> findAllAction() {
        List<ActionModel> listAction = new ArrayList<>();
        for (AppConstants.SYSTEM_MODULE sysModule : AppConstants.SYSTEM_MODULE.values()) {
            switch (sysModule) {
                case PRODUCT:
                    for (AppConstants.PRODUCT_ACTION sysAction : AppConstants.PRODUCT_ACTION.values()) {
                        listAction.add(new ActionModel(sysAction.name(), sysAction.getLabel(), sysModule.name()));
                    }
                    break;
                case STORAGE:
                    for (AppConstants.STORAGE_ACTION sysAction : AppConstants.STORAGE_ACTION.values()) {
                    	listAction.add(new ActionModel(sysAction.name(), sysAction.getLabel(), sysModule.name()));
                    }
                    break;
                case SYSTEM:
                    for (AppConstants.SYSTEM_ACTION sysAction : AppConstants.SYSTEM_ACTION.values()) {
                    	listAction.add(new ActionModel(sysAction.name(), sysAction.getLabel(), sysModule.name()));
                    }
                    break;
                case CATEGORY:
                    for (AppConstants.CATEGORY_ACTION sysAction : AppConstants.CATEGORY_ACTION.values()) {
                    	listAction.add(new ActionModel(sysAction.name(), sysAction.getLabel(), sysModule.name()));
                    }
                    break;
            }
        }
        return listAction;
    }

    @Override
    public AccountRole findById(Integer id) {
        return accountRoleRepo.findById(id).orElse(null);
    }

    @Override
    public List<AccountRole> findByAccountId(Integer accountId) {
        return accountRoleRepo.findByAccountId(accountId);
    }

    @Override
    public String updatePermission(String moduleKey, String actionKey, Integer accountId) {
        accountRoleRepo.save(new AccountRole(moduleKey, actionKey, accountId));
        return MessageUtils.UPDATE_SUCCESS;
    }

    @Override
    public boolean isAuthorized(int accountId, String module, String action) {
        return accountRoleRepo.isAuthorized(accountId, module, action) != null;
    }

    @Override
    public String deleteAllRole(Integer accountId) {
        accountRoleRepo.deleteByAccountId(accountId);
        return MessageUtils.DELETE_SUCCESS;
    }

    private FlowieeRole buildFlowieeRole(Integer pAccountId, String pModuleKey, String pModuleLabel, String pActionKey, String pActionLabel) {
        FlowieeRole flowieeRole = new FlowieeRole();

        ModuleModel module = new ModuleModel();
        module.setModuleKey(pModuleKey);
        module.setModuleLabel(pModuleLabel);
        flowieeRole.setModule(module);

        ActionModel action = new ActionModel();
        action.setActionKey(pActionKey);
        action.setActionLabel(pActionLabel);
        action.setModuleKey(pModuleKey);
        flowieeRole.setAction(action);

        AccountRole isAuthor = accountRoleRepo.isAuthorized(pAccountId, pModuleKey, pActionKey);
        flowieeRole.setIsAuthor(isAuthor != null);

        flowieeRole.setAccountId(pAccountId);

        return flowieeRole;
    }
}
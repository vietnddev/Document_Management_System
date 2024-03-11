package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.entity.Category;
import com.flowiee.dms.entity.SystemConfig;
import com.flowiee.dms.repository.FlowieeConfigRepository;
import com.flowiee.dms.service.CategoryService;
import com.flowiee.dms.service.ConfigService;
import com.flowiee.dms.service.LanguageService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConfigServiceImpl implements ConfigService {
    private final FlowieeConfigRepository sysConfigRepo;
    private final CategoryService categoryService;
    private final LanguageService languageService;

    @Autowired
    public ConfigServiceImpl(FlowieeConfigRepository sysConfigRepo, CategoryService categoryService, LanguageService languageService) {
        this.sysConfigRepo = sysConfigRepo;
        this.categoryService = categoryService;
        this.languageService = languageService;
    }

    @Override
    public SystemConfig findById(Integer id) {
        return sysConfigRepo.findById(id).orElse(null);
    }

    @Override
    public List<SystemConfig> findAll() {
        return sysConfigRepo.findAll();
    }

    @Override
    public SystemConfig save(SystemConfig systemConfig) {
        return sysConfigRepo.save(systemConfig);
    }

    @Override
    public SystemConfig update(SystemConfig systemConfig, Integer id) {
        systemConfig.setId(id);
        logger.info("Update config success! " + systemConfig.toString());
        return sysConfigRepo.save(systemConfig);
    }

    @Override
    public String delete(Integer id) {
        try {
            sysConfigRepo.deleteById(id);
            return MessageUtils.DELETE_SUCCESS;
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Transactional
    @Override
    public String refreshApp() {
        try {
            //
            List<Category> rootCategories = categoryService.findRootCategory();
            for (Category c : rootCategories) {
                if (c.getType() != null && !c.getType().trim().isEmpty()) {
                    AppConstants.CATEGORY.valueOf(c.getType()).setLabel(c.getName());
                }
            }
            //
            languageService.reloadMessage("vi");
            languageService.reloadMessage("en");
            return "Completed";
        } catch (RuntimeException ex) {
            logger.error("An error occurred while refresh app", ex);
            throw new AppException();
        }
    }
}
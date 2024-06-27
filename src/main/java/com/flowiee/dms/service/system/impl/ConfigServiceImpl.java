package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.repository.system.FlowieeConfigRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.ConfigService;
import com.flowiee.dms.service.system.LanguageService;
import com.flowiee.dms.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConfigServiceImpl extends BaseService implements ConfigService {
    LanguageService         languageService;
    FlowieeConfigRepository sysConfigRepo;

    @Override
    public Optional<SystemConfig> findById(Integer id) {
        return sysConfigRepo.findById(id);
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
            return MessageCode.DELETE_SUCCESS.getDescription();
        } catch (RuntimeException ex) {
            throw new AppException(ex);
        }
    }

    @Transactional
    @Override
    public String refreshApp() {
        try {
            //
            //List<Category> rootCategories = categoryService.findRootCategory();
            //for (Category c : rootCategories) {
            //    if (c.getType() != null && !c.getType().trim().isEmpty()) {
            //        AppConstants.CATEGORY.valueOf(c.getType()).setLabel(c.getName());
            //    }
            //}
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
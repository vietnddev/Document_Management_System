package com.flowiee.dms.base;

import com.flowiee.dms.entity.system.SystemConfig;
import com.flowiee.dms.repository.system.SystemConfigRepository;
import com.flowiee.dms.service.system.LanguageService;
import com.flowiee.dms.utils.FileUtils;
import com.flowiee.dms.utils.constants.ConfigCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
public class StartUp {
	private final LanguageService languageService;
	private final SystemConfigRepository configRepository;

    protected Logger logger = LoggerFactory.getLogger(getClass());
    public static Date START_APP_TIME = null;
    public static String mvResourceUploadPath = null;

	public StartUp(LanguageService languageService, SystemConfigRepository configRepository) {
		this.languageService = languageService;
		this.configRepository = configRepository;
	}
	
    @Bean
    CommandLineRunner init() {
    	return args -> {
            initData();
            loadLanguageMessages("en");
            loadLanguageMessages("vi");
            initResourceConfig();
            START_APP_TIME = new Date();
        };
    }

    private void initData() {
        String flagConfigCode = ConfigCode.initData.name();
        SystemConfig flagConfigObj = configRepository.findByCode(flagConfigCode);
        if (flagConfigObj == null) {
            List<SystemConfig> cnf = new ArrayList<>();
            cnf.add(initDefaultAudit(ConfigCode.initData, "Initialize initial data for the system", "Y"));
            cnf.add(initDefaultAudit(ConfigCode.emailHost, "Email host", "smtp"));
            cnf.add(initDefaultAudit(ConfigCode.emailPort, "Email port", "587"));
            cnf.add(initDefaultAudit(ConfigCode.emailUser, "Email username", null));
            cnf.add(initDefaultAudit(ConfigCode.emailPass, "Email password", null));
            cnf.add(initDefaultAudit(ConfigCode.sysTimeOut, "Thời gian timeout", "3600"));
            cnf.add(initDefaultAudit(ConfigCode.maxSizeFileUpload, "Dung lượng file tối đa cho phép upload", null));
            cnf.add(initDefaultAudit(ConfigCode.extensionAllowedFileUpload, "Định dạng file được phép upload", null));
            cnf.add(initDefaultAudit(ConfigCode.resourceUploadPath, "Thư mực chứa tệp upload", null));
            cnf.add(initDefaultAudit(ConfigCode.timeStorageFileInRecycleBin, "Thời gian lưu trữ tệp ở thùng rác", "15"));
            configRepository.saveAll(cnf);
        }
    }
    
    private void loadLanguageMessages(String langCode) {
        languageService.reloadMessage(langCode);
    }

    public void initResourceConfig() {
        SystemConfig systemConfig = configRepository.findByCode(ConfigCode.resourceUploadPath.name());
        if (systemConfig != null) {
            mvResourceUploadPath = systemConfig.getValue();
            logger.info(systemConfig.getCode() + ": " + mvResourceUploadPath);
        }

        Path templateTempForExportPath = FileUtils.getTemplateExportTempPath();
        Path folderTempForDownloadPath = FileUtils.getDownloadStorageTempPath();
        Path folderTempImportStoragePath = FileUtils.getImportStorageTempPath();
        try {
            if (!Files.exists(templateTempForExportPath)) {
                Files.createDirectories(templateTempForExportPath);
            }
            if (!Files.exists(folderTempForDownloadPath)) {
                Files.createDirectories(folderTempForDownloadPath);
            }
            if (!Files.exists(folderTempImportStoragePath)) {
                Files.createDirectories(folderTempImportStoragePath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SystemConfig initDefaultAudit(ConfigCode code, String name, String value) {
        SystemConfig systemConfig = new SystemConfig(code, name, value);
        systemConfig.setCreatedBy(-1);
        systemConfig.setLastUpdatedBy("SA");
        return systemConfig;
    }

    public static String getResourceUploadPath() {
        return mvResourceUploadPath;
    }
}
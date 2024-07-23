package com.flowiee.dms.base;

import com.flowiee.dms.service.system.LanguageService;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.FileUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Configuration
public class StartUp {
	private final LanguageService languageService;

	public StartUp(LanguageService languageService) {
		this.languageService = languageService;
	}
	
    @Bean
    CommandLineRunner init() {
    	return args -> {
            CommonUtils.START_APP_TIME = new Date();
            loadLanguageMessages("en");
            loadLanguageMessages("vi");
            initResourceConfig();
        };
    }
    
    private void loadLanguageMessages(String langCode) {
        languageService.reloadMessage(langCode);
    }

    public void initResourceConfig() {
        Path templateTempForExportPath = FileUtils.getTemplateExportTempPath();
        Path folderTempForDownloadPath = FileUtils.getDownloadStorageTempPath();
        try {
            if (!Files.exists(templateTempForExportPath)) {
                Files.createDirectory(templateTempForExportPath);
            }
            if (!Files.exists(folderTempForDownloadPath)) {
                Files.createDirectory(folderTempForDownloadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
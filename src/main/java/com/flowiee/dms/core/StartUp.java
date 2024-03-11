package com.flowiee.dms.core;

import com.flowiee.dms.service.LanguageService;
import com.flowiee.dms.utils.CommonUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        };
    }
    
    private void loadLanguageMessages(String langCode) {
        languageService.reloadMessage(langCode);
    }
}
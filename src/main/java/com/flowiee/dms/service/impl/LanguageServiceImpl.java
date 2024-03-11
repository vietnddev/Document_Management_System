package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.BadRequestException;
import com.flowiee.dms.entity.Language;
import com.flowiee.dms.repository.LanguagesRepository;
import com.flowiee.dms.service.LanguageService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Service
public class LanguageServiceImpl implements LanguageService {
	private final LanguagesRepository languagesRepo;
	
	public LanguageServiceImpl(LanguagesRepository languagesRepo) {
		this.languagesRepo = languagesRepo;
	}

	@Override
	public Language findById(Integer langId) {
		return languagesRepo.findById(langId).orElse(null);
	}

	@Override
	public Map<String, String> findAllLanguageMessages(String langCode) {
		List<Language> languageList = languagesRepo.findByCode(langCode);
        Map<String, String> languageMessages = new HashMap<>();
        for (Language language : languageList) {
            languageMessages.put(language.getKey(), language.getValue());
        }
        return languageMessages;
	}

	@Override
	public Language update(Language language, Integer langId) {
		if (langId == null || langId <= 0) {
			throw new BadRequestException();
		}
		return languagesRepo.save(language);
	}

	@Override
	public void reloadMessage(String langCode) {
		try {
			Map<String, String> enMessages = this.findAllLanguageMessages(langCode);
			Properties properties = new Properties();
			OutputStream outputStream = new FileOutputStream(String.format("src/main/resources/language/messages_%s.properties", langCode));
			for (Map.Entry<String, String> entry : enMessages.entrySet()) {
				properties.setProperty(entry.getKey(), entry.getValue());
			}
			properties.store(outputStream, String.format("%s Messages", langCode));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
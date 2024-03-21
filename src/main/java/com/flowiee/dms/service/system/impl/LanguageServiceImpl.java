package com.flowiee.dms.service.system.impl;

import com.flowiee.dms.exception.AppException;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.entity.system.Language;
import com.flowiee.dms.repository.system.LanguagesRepository;
import com.flowiee.dms.service.system.LanguageService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

@Service
public class LanguageServiceImpl implements LanguageService {
	private final LanguagesRepository languagesRepo;
	
	public LanguageServiceImpl(LanguagesRepository languagesRepo) {
		this.languagesRepo = languagesRepo;
	}

	@Override
	public Optional<Language> findById(Integer langId) {
		return languagesRepo.findById(langId);
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
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
}
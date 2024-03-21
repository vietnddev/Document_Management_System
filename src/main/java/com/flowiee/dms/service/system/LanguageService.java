package com.flowiee.dms.service.system;

import com.flowiee.dms.entity.system.Language;

import java.util.Map;
import java.util.Optional;

public interface LanguageService {
	Optional<Language> findById(Integer langId);
	
	Map<String, String> findAllLanguageMessages(String langCode);
	
	Language update(Language language, Integer langId);

	void reloadMessage(String langCode);
}
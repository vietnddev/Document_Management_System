package com.flowiee.dms.service;

import com.flowiee.dms.entity.Language;

import java.util.Map;

public interface LanguageService {
	Language findById(Integer langId);
	
	Map<String, String> findAllLanguageMessages(String langCode);
	
	Language update(Language language, Integer langId);

	void reloadMessage(String langCode);
}
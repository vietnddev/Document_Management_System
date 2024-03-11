package com.flowiee.dms.repository;

import com.flowiee.dms.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LanguagesRepository extends JpaRepository<Language, Integer> {	
	List<Language> findByCode(String code);
}
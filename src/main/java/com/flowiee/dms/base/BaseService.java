package com.flowiee.dms.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public interface BaseService<T> {
    Logger logger = LoggerFactory.getLogger(BaseService.class);

    Optional<T> findById(Integer entityId);

    T save(T entity);

    T update(T entity, Integer entityId);

    String delete(Integer entityId);
}
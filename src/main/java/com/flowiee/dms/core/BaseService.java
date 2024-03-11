package com.flowiee.dms.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface BaseService<T> {
    Logger logger = LoggerFactory.getLogger(BaseService.class);

    T findById(Integer entityId);

    T save(T entity);

    T update(T entity, Integer entityId);

    String delete(Integer entityId);
}
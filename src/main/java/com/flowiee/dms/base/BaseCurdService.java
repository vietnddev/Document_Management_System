package com.flowiee.dms.base;

import java.util.Optional;

public interface BaseCurdService<T> {
    Optional<T> findById(Integer entityId);

    T save(T entity);

    T update(T entity, Integer entityId);

    String delete(Integer entityId);
}
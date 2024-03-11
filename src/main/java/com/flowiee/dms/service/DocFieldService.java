package com.flowiee.dms.service;

import com.flowiee.dms.core.BaseService;
import com.flowiee.dms.entity.DocField;

import java.util.List;

public interface DocFieldService extends BaseService<DocField> {
    List<DocField> findAll();

    List<DocField> findByDocTypeId(Integer doctypeId);
}
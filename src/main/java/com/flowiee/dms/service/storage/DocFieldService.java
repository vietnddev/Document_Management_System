package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseService;
import com.flowiee.dms.entity.storage.DocField;

import java.util.List;

public interface DocFieldService extends BaseService<DocField> {
    List<DocField> findAll();

    List<DocField> findByDocTypeId(Integer doctypeId);
}
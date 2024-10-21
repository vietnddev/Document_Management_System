package com.flowiee.dms.service.storage;

import com.flowiee.dms.base.BaseCurdService;
import com.flowiee.dms.entity.storage.DocField;

import java.util.List;

public interface DocFieldService extends BaseCurdService<DocField> {
    List<DocField> findAll();

    List<DocField> findByDocTypeId(Long doctypeId);
}
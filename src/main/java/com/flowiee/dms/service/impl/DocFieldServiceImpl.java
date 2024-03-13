package com.flowiee.dms.service.impl;

import com.flowiee.dms.core.exception.DataInUseException;
import com.flowiee.dms.entity.DocField;
import com.flowiee.dms.model.ACTION;
import com.flowiee.dms.repository.DocFieldRepository;
import com.flowiee.dms.service.DocDataService;
import com.flowiee.dms.service.DocFieldService;
import com.flowiee.dms.service.SystemLogService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocFieldServiceImpl implements DocFieldService {
    private static final String module = AppConstants.SYSTEM_MODULE.STORAGE.name();

    private final DocFieldRepository docFieldRepository;
    private final DocDataService docDataService;
    private final SystemLogService systemLogService;

    @Autowired
    public DocFieldServiceImpl(DocFieldRepository docFieldRepository, DocDataService docDataService, SystemLogService systemLogService) {
        this.docFieldRepository = docFieldRepository;
        this.docDataService = docDataService;
        this.systemLogService = systemLogService;
    }

    @Override
    public List<DocField> findAll() {
        return docFieldRepository.findAll();
    }

    @Override
    public DocField findById(Integer id) {
        return docFieldRepository.findById(id).orElse(null);
    }

    @Override
    public List<DocField> findByDocTypeId(Integer doctypeId) {
        return docFieldRepository.findByDoctype(doctypeId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DocField save(DocField docField) {
        DocField docFieldSaved = docFieldRepository.save(docField);
        systemLogService.writeLog(module, ACTION.STG_DOC_DOCTYPE_CONFIG.name(), "Thêm mới doc_field id=" + docField.getId(), null);
        logger.info(DocumentServiceImpl.class.getName() + ": Thêm mới doc_field id=" + docField.getId());
        return docFieldSaved;
    }

    @Override
    public DocField update(DocField docField, Integer docFieldId) {
        docField.setId(docFieldId);
        DocField docFieldUpdated = docFieldRepository.save(docField);
        systemLogService.writeLog(module, ACTION.STG_DOC_DOCTYPE_CONFIG.name(), "Cập nhật doc_field id=" + docFieldId, null);
        logger.info(DocumentServiceImpl.class.getName() + ": Cập nhật doc_field " + docFieldId);
        return docFieldUpdated;
    }

    @Transactional
    @Override
    public String delete(Integer id) {
        if (!docDataService.findByDocField(id).isEmpty()) {
            throw new DataInUseException(MessageUtils.ERROR_DATA_LOCKED);
        }
        docFieldRepository.deleteById(id);
        systemLogService.writeLog(module, ACTION.STG_DOC_DOCTYPE_CONFIG.name(), "Xóa doc_field id=" + id, null);
        logger.info(DocumentServiceImpl.class.getName() + ": Xóa doc_field id=" + id);
        return MessageUtils.DELETE_SUCCESS;
    }
}
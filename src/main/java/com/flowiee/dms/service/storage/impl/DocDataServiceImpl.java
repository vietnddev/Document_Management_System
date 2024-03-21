package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.repository.storage.DocDataRepository;
import com.flowiee.dms.service.storage.DocDataService;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DocDataServiceImpl implements DocDataService {
    @Autowired
    private DocDataRepository docDataRepository;

    public List<DocData> findAll() {
        return docDataRepository.findAll();
    }

    public Optional<DocData> findById(Integer id) {
        return docDataRepository.findById(id);
    }

    public DocData save(DocData docData) {
        return docDataRepository.save(docData);
    }

    @Override
    public DocData update(DocData entity, Integer entityId) {
        entity.setId(entityId);
        return docDataRepository.save(entity);
    }

    public String delete(Integer id) {
        docDataRepository.deleteById(id);
        return MessageUtils.DELETE_SUCCESS;
    }

    @Override
    public List<DocData> findByDocField(Integer docFieldId) {
        return docDataRepository.findByDocField(docFieldId);
    }

    @Override
    public List<DocData> findByDocument(Integer documentId) {
        return docDataRepository.findByDocumentId(documentId);
    }

    @Override
    public DocData findByFieldIdAndDocId(Integer docFieldId, Integer documentId) {
        return docDataRepository.findByFieldIdAndDocId(docFieldId, documentId);
    }

    @Transactional
    @Override
    public String update(String value, Integer docDataId) {
        docDataRepository.updateMetaData(value, docDataId);
        return MessageUtils.UPDATE_SUCCESS;
    }
}
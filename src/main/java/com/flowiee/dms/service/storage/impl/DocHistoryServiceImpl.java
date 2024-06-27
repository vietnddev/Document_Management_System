package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocHistory;
import com.flowiee.dms.repository.storage.DocHistoryRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocHistoryService;
import com.flowiee.dms.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocHistoryServiceImpl extends BaseService implements DocHistoryService {
    DocHistoryRepository docHistoryRepo;

    @Override
    public List<DocHistory> findAll() {
        return docHistoryRepo.findAll();
    }

    @Override
    public Optional<DocHistory> findById(Integer docHistoryId) {
        return docHistoryRepo.findById(docHistoryId);
    }

    @Override
    public DocHistory save(DocHistory docHistory) {
        return docHistoryRepo.save(docHistory);
    }

    @Override
    public DocHistory update(DocHistory docHistory, Integer docHistoryId) {
        docHistory.setId(docHistoryId);
        return docHistoryRepo.save(docHistory);
    }

    @Override
    public String delete(Integer docHistoryId) {
        docHistoryRepo.deleteById(docHistoryId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }

    @Override
    public List<DocHistory> findByDocData(Integer docDataId) {
        return docHistoryRepo.findByDocData(docDataId);
    }
}
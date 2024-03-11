package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.DocShare;
import com.flowiee.dms.repository.DocShareRepository;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.service.DocShareService;
import com.flowiee.dms.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocShareServiceImpl implements DocShareService {
    @Autowired
    private DocShareRepository docShareRepository;

    @Autowired
    private AccountService accountService;

    @Override
    public List<DocShare> findAll() {
        return docShareRepository.findAll();
    }

    @Override
    public DocShare findById(int id) {
        return docShareRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isShared(int documentId) {
        if (CommonUtils.ADMINISTRATOR.equals(CommonUtils.getCurrentAccountUsername())) {
            return true;
        }
        return docShareRepository.findByDocAndAccount(documentId, CommonUtils.getCurrentAccountId()) != null;
    }
}
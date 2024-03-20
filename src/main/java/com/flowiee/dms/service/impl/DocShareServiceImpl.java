package com.flowiee.dms.service.impl;

import com.flowiee.dms.entity.Account;
import com.flowiee.dms.entity.DocShare;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.repository.DocShareRepository;
import com.flowiee.dms.service.AccountService;
import com.flowiee.dms.service.DocShareService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocShareServiceImpl implements DocShareService {
    @Autowired private DocShareRepository docShareRepo;
    @Autowired private AccountService accountService;

    @Override
    public List<DocShare> findAll() {
        return docShareRepo.findAll();
    }

    @Override
    public List<DocShareModel> findDetailRolesOfDocument(Integer docId) {
        List<DocShareModel> lsModel = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            DocShareModel model = new DocShareModel();
            model.setDocumentId(docId);
            model.setAccountId(account.getId());
            model.setAccountName(account.getFullName());
            for (DocShare docShare : docShareRepo.findByDocAndAccount(docId, account.getId())) {
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_READ.equals(docShare.getRole())) model.setCanRead(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_UPDATE.equals(docShare.getRole())) model.setCanUpdate(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_DELETE.equals(docShare.getRole())) model.setCanDelete(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_MOVE.equals(docShare.getRole())) model.setCanMove(true);
                if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername()) || AppConstants.DOC_RIGHT_SHARE.equals(docShare.getRole())) model.setCanShare(true);
            }
            lsModel.add(model);
        }
        return lsModel;
    }

    @Override
    public DocShare findById(Integer id) {
        return docShareRepo.findById(id).orElse(null);
    }

    @Override
    public boolean isShared(int documentId) {
        if (CommonUtils.ADMIN.equals(CommonUtils.getUserPrincipal().getUsername())) {
            return true;
        }
        return docShareRepo.findByDocAndAccount(documentId, CommonUtils.getUserPrincipal().getId()) != null;
    }

    @Transactional
    @Override
    public void deleteByAccount(Integer accountId) {
        docShareRepo.deleteAllByAccount(accountId);
    }

    @Transactional
    @Override
    public void deleteByDocument(Integer documentId) {
        docShareRepo.deleteAllByDocument(documentId);
    }

    @Override
    public DocShare save(DocShare entity) {
        return docShareRepo.save(entity);
    }

    @Override
    public DocShare update(DocShare entity, Integer entityId) {
        entity.setId(entityId);
        return docShareRepo.save(entity);
    }

    @Override
    public String delete(Integer entityId) {
        docShareRepo.deleteById(entityId);
        return MessageUtils.DELETE_SUCCESS;
    }
}
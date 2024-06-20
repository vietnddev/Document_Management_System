package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.repository.storage.DocShareRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.storage.DocShareService;;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.MessageUtils;
import com.flowiee.dms.utils.constants.DocRight;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocShareServiceImpl extends BaseService implements DocShareService {
    AccountService     accountService;
    DocShareRepository docShareRepository;

    @Override
    public List<DocShare> findAll() {
        return docShareRepository.findAll();
    }

    @Override
    public List<DocShareModel> findDetailRolesOfDocument(Integer docId) {
        List<DocShareModel> lsModel = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            if (account.getUsername().equals(AppConstants.ADMINISTRATOR)) {
                continue;
            }
            DocShareModel model = new DocShareModel();
            model.setDocumentId(docId);
            model.setAccountId(account.getId());
            model.setAccountName(account.getFullName());
            for (DocShare docShare : docShareRepository.findByDocAndAccount(docId, account.getId())) {
                if (AppConstants.ADMINISTRATOR.equals(account.getUsername()) || DocRight.READ.getValue().equals(docShare.getRole())) model.setCanRead(true);
                if (AppConstants.ADMINISTRATOR.equals(account.getUsername()) || DocRight.UPDATE.getValue().equals(docShare.getRole())) model.setCanUpdate(true);
                if (AppConstants.ADMINISTRATOR.equals(account.getUsername()) || DocRight.DELETE.getValue().equals(docShare.getRole())) model.setCanDelete(true);
                if (AppConstants.ADMINISTRATOR.equals(account.getUsername()) || DocRight.MOVE.getValue().equals(docShare.getRole())) model.setCanMove(true);
                if (AppConstants.ADMINISTRATOR.equals(account.getUsername()) || DocRight.SHARE.getValue().equals(docShare.getRole())) model.setCanShare(true);
            }
            lsModel.add(model);
        }
        return lsModel;
    }

    @Override
    public Optional<DocShare> findById(Integer id) {
        return docShareRepository.findById(id);
    }

    @Override
    public boolean isShared(int documentId) {
        if (AppConstants.ADMINISTRATOR.equals(CommonUtils.getUserPrincipal().getUsername())) {
            return true;
        }
        return docShareRepository.findByDocAndAccount(documentId, CommonUtils.getUserPrincipal().getId()) != null;
    }

    @Transactional
    @Override
    public void deleteByAccount(Integer accountId) {
        docShareRepository.deleteAllByAccount(accountId);
    }

    @Transactional
    @Override
    public void deleteByDocument(Integer documentId) {
        docShareRepository.deleteAllByDocument(documentId);
    }

    @Override
    public DocShare save(DocShare entity) {
        return docShareRepository.save(entity);
    }

    @Override
    public DocShare update(DocShare entity, Integer entityId) {
        entity.setId(entityId);
        return docShareRepository.save(entity);
    }

    @Override
    public String delete(Integer entityId) {
        docShareRepository.deleteById(entityId);
        return MessageUtils.DELETE_SUCCESS;
    }
}
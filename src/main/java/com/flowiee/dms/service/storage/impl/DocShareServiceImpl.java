package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.system.Account;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocShareRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.system.AccountService;
import com.flowiee.dms.service.storage.DocShareService;;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import com.flowiee.dms.utils.constants.DocRight;
import com.flowiee.dms.utils.constants.MessageCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DocShareServiceImpl extends BaseService implements DocShareService {
    AccountService      accountService;
    DocShareRepository  docShareRepository;
    @NonFinal
    @Lazy
    @Autowired
    DocumentInfoService documentInfoService;

    @Override
    public List<DocShare> findAll() {
        return docShareRepository.findAll();
    }

    @Override
    public List<DocShareModel> findDetailRolesOfDocument(Long docId) {
        List<DocShareModel> lsModel = new ArrayList<>();
        for (Account account : accountService.findAll()) {
            if (account.getUsername().equals(AppConstants.ADMINISTRATOR)) {
                continue;
            }
            DocShareModel model = new DocShareModel();
            model.setDocumentId(docId);
            model.setAccountId(account.getId());
            model.setAccountName(account.getFullName());
            for (DocShare docShare : docShareRepository.findByDocAndAccount(docId, account.getId(), null)) {
                boolean isAdmin = AppConstants.ADMINISTRATOR.equals(account.getUsername());
                if (isAdmin || DocRight.READ.getValue().equals(docShare.getRole()))
                    model.setCanRead(true);
                if (isAdmin || DocRight.UPDATE.getValue().equals(docShare.getRole()))
                    model.setCanUpdate(true);
                if (isAdmin || DocRight.DELETE.getValue().equals(docShare.getRole()))
                    model.setCanDelete(true);
                if (isAdmin || DocRight.MOVE.getValue().equals(docShare.getRole()))
                    model.setCanMove(true);
                if (isAdmin || DocRight.SHARE.getValue().equals(docShare.getRole()))
                    model.setCanShare(true);
            }
            lsModel.add(model);
        }
        return lsModel;
    }

    @Override
    public Optional<DocShare> findById(Long id) {
        return docShareRepository.findById(id);
    }

    @Override
    public boolean isShared(long documentId, String role) {
        if (AppConstants.ADMINISTRATOR.equals(CommonUtils.getUserPrincipal().getUsername())) {
            return true;
        }
        List<DocShare> docShares = docShareRepository.findByDocAndAccount(documentId, CommonUtils.getUserPrincipal().getId(), role);
        if (ObjectUtils.isNotEmpty(docShares)) {
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public void deleteByAccount(Long accountId) {
        docShareRepository.deleteAllByAccount(accountId);
    }

    @Transactional
    @Override
    public void deleteByDocument(Long documentId) {
        docShareRepository.deleteAllByDocument(documentId);
    }

    @Transactional
    @Override
    public void deleteAllByDocument(Long documentId) {
        docShareRepository.deleteAllByDocument(documentId);
        List<DocumentDTO> allSubDocs = documentInfoService.findSubDocByParentId(documentId, null, true, true, false);
        for (DocumentDTO dto : allSubDocs) {
            docShareRepository.deleteAllByDocument(dto.getId());
        }
    }

    @Override
    public DocShare save(DocShare entity) {
        return docShareRepository.save(entity);
    }

    @Override
    public DocShare update(DocShare entity, Long entityId) {
        entity.setId(entityId);
        return docShareRepository.save(entity);
    }

    @Override
    public String delete(Long entityId) {
        docShareRepository.deleteById(entityId);
        return MessageCode.DELETE_SUCCESS.getDescription();
    }
}
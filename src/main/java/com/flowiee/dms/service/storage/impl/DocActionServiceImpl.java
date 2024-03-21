package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.storage.DocActionService;
import com.flowiee.dms.service.storage.DocDataService;
import com.flowiee.dms.service.storage.DocShareService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.utils.AppConstants;
import com.flowiee.dms.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocActionServiceImpl implements DocActionService {
    @Autowired
    private DocumentInfoService documentInfoService;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocShareService docShareService;
    @Autowired
    private DocDataService docDataService;

    @Override
    public DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy) {
        Optional<Document> doc = documentInfoService.findById(docId);
        if (doc.isEmpty()) {
            throw new BadRequestException();
        }
        //Copy doc
        doc.get().setId(0);
        doc.get().setName(nameCopy);
        doc.get().setAsName(CommonUtils.generateAliasName(nameCopy));
        Document docCopied = documentInfoService.save(doc.get());
        //Copy metadata
        for (DocData docData : docDataService.findByDocument(docId)) {
            docData.setId(0);
            docData.setDocument(docCopied);
            docDataService.save(docData);
        }
        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Integer docId, Integer destinationId) {
        documentRepository.updateParentId(destinationId, docId);
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Integer docId, List<DocShareModel> accountShares) {
        Optional<Document> doc = documentInfoService.findById(docId);
        if (doc.isEmpty() || accountShares.isEmpty()) {
            throw new BadRequestException();
        }
        List<DocShare> docShared = new ArrayList<>();
        docShareService.deleteByDocument(docId);
        for (DocShareModel model : accountShares) {
            int accountId = model.getAccountId();
            if (model.getCanRead()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_READ)));
            }
            if (model.getCanUpdate()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_UPDATE)));
            }
            if (model.getCanDelete()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_DELETE)));
            }
            if (model.getCanMove()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_MOVE)));
            }
            if (model.getCanShare()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, AppConstants.DOC_RIGHT_SHARE)));
            }
        }
        return docShared;
    }
}
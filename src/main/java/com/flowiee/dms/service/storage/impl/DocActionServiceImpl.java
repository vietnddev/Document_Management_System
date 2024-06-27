package com.flowiee.dms.service.storage.impl;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.exception.BadRequestException;
import com.flowiee.dms.exception.ResourceNotFoundException;
import com.flowiee.dms.model.DocShareModel;
import com.flowiee.dms.model.dto.DocumentDTO;
import com.flowiee.dms.repository.storage.DocumentRepository;
import com.flowiee.dms.service.BaseService;
import com.flowiee.dms.service.storage.DocActionService;
import com.flowiee.dms.service.storage.DocDataService;
import com.flowiee.dms.service.storage.DocShareService;
import com.flowiee.dms.service.storage.DocumentInfoService;
import com.flowiee.dms.service.system.RoleService;
import com.flowiee.dms.utils.CommonUtils;
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
public class DocActionServiceImpl extends BaseService implements DocActionService {
    DocDataService      docDataService;
    DocShareService     docShareService;
    DocumentRepository  documentRepository;
    DocumentInfoService documentInfoService;

    @Override
    public DocumentDTO copyDoc(Integer docId, Integer destinationId, String nameCopy) {
        Optional<DocumentDTO> doc = documentInfoService.findById(docId);
        if (doc.isEmpty()) {
            throw new BadRequestException("Document to copy not found!");
        }
        //Copy doc
        doc.get().setId(null);
        doc.get().setName(nameCopy);
        doc.get().setAsName(CommonUtils.generateAliasName(nameCopy));
        Document docCopied = documentInfoService.save(doc.get());
        //Copy metadata
        for (DocData docData : docDataService.findByDocument(docId)) {
            DocData docDataNew = DocData.builder()
                    .docField(docData.getDocField())
                    .document(docCopied)
                    .value(docData.getValue())
                    .build();
            docDataService.save(docDataNew);
        }
        return DocumentDTO.fromDocument(docCopied);
    }

    @Transactional
    @Override
    public String moveDoc(Integer docId, Integer destinationId) {
        Optional<Document> docToMove = documentRepository.findById(docId);
        if (docToMove.isEmpty()) {
            throw new ResourceNotFoundException("Document to move not found!");
        }
        if (documentInfoService.findById(destinationId).isEmpty()) {
            throw new ResourceNotFoundException("Document move to found!");
        }
        docToMove.get().setParentId(destinationId);
        documentRepository.save(docToMove.get());
        return "Move successfully!";
    }

    @Transactional
    @Override
    public List<DocShare> shareDoc(Integer docId, List<DocShareModel> accountShares) {
        Optional<DocumentDTO> doc = documentInfoService.findById(docId);
        if (doc.isEmpty() || accountShares.isEmpty()) {
            throw new ResourceNotFoundException("Document not found!");
        }
        List<DocShare> docShared = new ArrayList<>();
        docShareService.deleteByDocument(docId);
        for (DocShareModel model : accountShares) {
            int accountId = model.getAccountId();
            if (model.getCanRead()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.READ.getValue())));

            }
            if (model.getCanUpdate()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.UPDATE.getValue())));
            }
            if (model.getCanDelete()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.DELETE.getValue())));
            }
            if (model.getCanMove()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.MOVE.getValue())));
            }
            if (model.getCanShare()) {
                docShared.add(docShareService.save(new DocShare(docId, accountId, DocRight.SHARE.getValue())));
            }
        }
        return docShared;
    }
}
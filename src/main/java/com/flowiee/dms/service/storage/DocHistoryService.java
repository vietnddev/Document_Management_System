package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.entity.storage.DocHistory;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.utils.ChangeLog;

import java.util.List;

public interface DocHistoryService {
    List<DocHistory> findAll();

    List<DocHistory> findByDocData(Long docDataId);

    DocHistory save(DocHistory docHistory);

    List<DocHistory> save(Document document, DocData docData, FileStorage fileStorage, ChangeLog changeLog, String title);

    DocHistory saveDocDataHistory(Document document, DocData docData, String field, Object oldValue, Object newValue);
}
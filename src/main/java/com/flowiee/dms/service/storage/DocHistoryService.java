package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.DocData;
import com.flowiee.dms.entity.storage.StorageHistory;
import com.flowiee.dms.entity.storage.Document;
import com.flowiee.dms.entity.storage.FileStorage;
import com.flowiee.dms.utils.ChangeLog;

import java.util.List;

public interface DocHistoryService {
    List<StorageHistory> findAll();

    List<StorageHistory> findByDocData(Long docDataId);

    StorageHistory save(StorageHistory storageHistory);

    List<StorageHistory> save(Document document, DocData docData, FileStorage fileStorage, ChangeLog changeLog, String title);

    StorageHistory saveDocDataHistory(Document document, DocData docData, String field, Object oldValue, Object newValue);
}
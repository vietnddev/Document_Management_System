package com.flowiee.dms.service.storage;

import com.flowiee.dms.entity.storage.DocVersion;

import java.io.IOException;

public interface DocArchiveService {
    long getNextDocVersion(long documentId);

    long getLatestDocVersion(long documentId);

    void archiveVersion(long documentId, DocVersion docVersion) throws IOException;

    void restoreOldVersion(long documentId, long versionId);
}
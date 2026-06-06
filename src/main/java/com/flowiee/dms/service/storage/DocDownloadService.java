package com.flowiee.dms.service.storage;

import com.flowiee.dms.model.DownloadResource;

import java.io.IOException;

public interface DocDownloadService {
    DownloadResource download(long documentId)  throws IOException;
}
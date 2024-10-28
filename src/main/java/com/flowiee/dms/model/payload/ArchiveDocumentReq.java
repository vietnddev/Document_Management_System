package com.flowiee.dms.model.payload;

import lombok.Data;

@Data
public class ArchiveDocumentReq {
    private long documentId;
    private String versionName;
}
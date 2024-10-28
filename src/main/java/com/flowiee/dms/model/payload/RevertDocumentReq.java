package com.flowiee.dms.model.payload;

import lombok.Data;

@Data
public class RevertDocumentReq {
    private long documentId;
    private long versionId;
}
package com.flowiee.dms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocShareModel {
    Integer documentId;
    Integer accountId;
    String accountName;
    Boolean canRead;
    Boolean canUpdate;
    Boolean canDelete;
    Boolean canMove;
    Boolean canShare;

    public DocShareModel() {
        canRead = false;
        canUpdate = false;
        canDelete = false;
        canMove = false;
        canShare = false;
    }
}
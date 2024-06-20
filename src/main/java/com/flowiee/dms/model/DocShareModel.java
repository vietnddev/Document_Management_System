package com.flowiee.dms.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
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
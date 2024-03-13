package com.flowiee.dms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocShareModel {
    Integer documentId;
    Integer accountId;
    String accountName;
    Boolean doRead;
    Boolean doUpdate;
    Boolean doDelete;
    Boolean doMove;
    Boolean doShare;

    public DocShareModel() {
        doRead = false;
        doUpdate = false;
        doDelete = false;
        doMove = false;
        doShare = false;
    }
}
package com.flowiee.dms.model.payload;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class MoveDocumentReq implements Serializable {
    int destinationId;
    List<Integer> selectedDocuments;
}
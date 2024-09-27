package com.flowiee.dms.model.payload;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DownloadDocumentReq {
    List<Integer> selectedDocuments;
}
package com.flowiee.dms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryStorage {
    private int totalDocument;
    private int totalFolder;
    private int totalFile;
    private String totalSize;
}
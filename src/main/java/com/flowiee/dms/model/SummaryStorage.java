package com.flowiee.dms.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SummaryStorage {
    int totalDocument;
    int totalFolder;
    int totalFile;
    String totalSize;
}
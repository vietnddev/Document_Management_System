package com.flowiee.dms.model;

import lombok.Getter;

@Getter
public enum FILE_EXTENSION {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    PDF("pdf"),
    XLS("xls"),
    XLSX("xlsx"),
    PPT("ppt"),
    PPTX("pptx"),
    DOC("doc"),
    DOCX("docx");

    private final String label;

    FILE_EXTENSION(String label) {
        this.label = label;
    }
}
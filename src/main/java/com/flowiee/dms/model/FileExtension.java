package com.flowiee.dms.model;

public enum FileExtension {
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

    private final String key;

    FileExtension(String key) {
        this.key = key;
    }

    public String key() {
        return this.name().toLowerCase();
    }
}
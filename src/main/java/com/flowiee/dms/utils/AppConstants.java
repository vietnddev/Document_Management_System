package com.flowiee.dms.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppConstants {
    public static String DOC_RIGHT_READ = "R";
    public static String DOC_RIGHT_UPDATE = "U";
    public static String DOC_RIGHT_DELETE = "D";
    public static String DOC_RIGHT_MOVE = "M";
    public static String DOC_RIGHT_SHARE = "S";

    @Getter
    public enum CATEGORY {
        DOCUMENT_TYPE("document-type", "DOCUMENT_TYPE", "Loại tài liệu");

        private final String key;
        private final String name;
        @Setter
        private String label;

        CATEGORY(String key, String name, String label) {
            this.key = key;
            this.name = name;
            this.label = label;
        }
    }
}
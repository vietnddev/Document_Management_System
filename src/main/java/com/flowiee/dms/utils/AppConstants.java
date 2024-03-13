package com.flowiee.dms.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppConstants {
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

    @Getter
    public enum SYSTEM_MODULE {
        STORAGE("KHO TÀI LIỆU"),

        SYSTEM("HỆ THỐNG"),

        CATEGORY("DANH MỤC");

        private final String label;

        SYSTEM_MODULE(String label) {
            this.label = label;
        }

        public static SYSTEM_MODULE valueOfLabel(String label) {
            for (SYSTEM_MODULE e : values()) {
                if (e.label.equals(label)) {
                    return e;
                }
            }
            return null;
        }

        public static List<String> getAllValue() {
            List<String> listValue = new ArrayList<>();
            for (SYSTEM_MODULE e : values()) {
                listValue.add(e.label);
            }
            return listValue;
        }

        public static LinkedHashMap<String, String> getAll() {
            LinkedHashMap<String, String> hm = new LinkedHashMap<>();
            for (SYSTEM_MODULE e : values()) {
                hm.put(e.name(), e.label);
            }
            return hm;
        }
    }

    @Getter
    public enum DASHBOARD_ACTION {
        READ_DASHBOARD("Xem dashboard");

        DASHBOARD_ACTION(String label) {
            this.label = label;
        }

        private final String label;
    }
}
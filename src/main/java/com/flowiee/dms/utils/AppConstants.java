package com.flowiee.dms.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AppConstants {
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
        PRODUCT("SẢN PHẨM"),

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
    public enum CATEGORY_ACTION {
        CTG_READ("Xem danh mục hệ thống"),
        CTG_CREATE("Thêm mới danh mục"),
        CTG_UPDATE("Cập nhật danh mục"),
        CTG_DELETE("Xóa danh mục"),
        CTG_IMPORT("Import danh mục"),
        CTG_EXPORT("Export danh mục");

        CATEGORY_ACTION(String label) {
            this.label = label;
        }

        private final String label;

    }

    @Getter
    public enum SYSTEM_ACTION {
        SYS_ROLE_READ("Xem quyền hệ thống"),
        SYS_LOG_READ("Xem nhật ký hệ thống"),
        SYS_LOGIN("Đăng nhập"),
        SYS_LOGOUT("Đăng xuất"),
        SYS_RESET_PASSWORD("Đổi mật khẩu"),

        SYS_ACCOUNT_READ("Xem danh sách tài khoản"),
        SYS_ACCOUNT_CREATE("Thêm mới tài khoản"),
        SYS_ACCOUNT_UPDATE("Cập nhật tài khoản"),
        SYS_ACCOUNT_DELETE("Xóa tài khoản"),
        SYS_ACCOUNT_RESET_PASSWORD("Reset mật khẩu tài khoản"),
        SYS_ACCOUNT_SHARE_ROLE("Phân quyền tài khoản");

        SYSTEM_ACTION(String label) {
            this.label = label;
        }

        private final String label;
    }

    @Getter
    public enum STORAGE_ACTION {
        STG_DASHBOARD("Xem dashboard STG"),
        STG_DOC_READ("Xem danh sách tài liệu"),
        STG_DOC_CREATE("Thêm mới tài liệu"),
        STG_DOC_UPDATE("Cập nhật tài liệu"),
        STG_DOC_DELETE("Xóa tài liệu"),
        STG_DOC_MOVE("Di chuyển tài liệu"),
        STG_DOC_COPY("Copy tài liệu"),
        STG_DOC_DOWNLOAD("Download tài liệu"),
        STG_DOC_SHARE("Chia sẽ tài liệu"),
        STG_DOC_DOCTYPE_CONFIG("Cấu hình loại tài liệu"),
        STG_MATERIAL_READ("Xem danh sách nguyên vật liệu"),
        STG_MATERIAL_CREATE("Thêm mới nguyên vật liệu"),
        STG_MATERIAL_UPDATE("Cập nhật nguyên vật liệu"),
        STG_MATERIAL_DELETE("Xóa nguyên vật liệu"),
        STG_TICKET_IMPORT_GOODS("Nhập hàng"),
        STG_TICKET_EXPORT_GOODS("Xuất hàng");

        STORAGE_ACTION(String label) {
            this.label = label;
        }

        private final String label;
    }

    @Getter
    public enum PRODUCT_ACTION {
        PRO_PRODUCT_READ("Xem danh sách sản phẩm"),
        PRO_PRODUCT_CREATE("Thêm mới sản phẩm"),
        PRO_PRODUCT_UPDATE("Cập nhật sản phẩm"),
        PRO_PRODUCT_DELETE("Xóa sản phẩm"),
        PRO_PRODUCT_IMPORT("Import sản phẩm"),
        PRO_PRODUCT_EXPORT("Export sản phẩm"),
        PRO_PRODUCT_PRICE("Quản lý giá bán"),
        PRO_PRODUCT_REPORT("Báo cáo thống kê sản phẩm"),

        PRO_ORDERS_READ("Xem danh sách"),
        PRO_ORDERS_CREATE("Thêm mới"),
        PRO_ORDERS_UPDATE("Cập nhật"),
        PRO_ORDERS_DELETE("Xóa"),
        PRO_ORDERS_EXPORT(""),

        PRO_CUSTOMER_READ("Xem danh sách đơn hàng"),
        PRO_CUSTOMER_CREATE("Thêm mới đơn hàng"),
        PRO_CUSTOMER_UPDATE("Cập nhật đơn hàng"),
        PRO_CUSTOMER_DELETE("Xóa đơn hàng"),

        PRO_SUPPLIER_READ("Xem danh sách supplier"),
        PRO_SUPPLIER_CREATE("Thêm mới supplier"),
        PRO_SUPPLIER_UPDATE("Cập nhật supplier"),
        PRO_SUPPLIER_DELETE("Xóa supplier"),

        PRO_VOUCHER_READ("Xem danh sách voucher"),
        PRO_VOUCHER_CREATE("Thêm mới voucher"),
        PRO_VOUCHER_UPDATE("Cập nhật voucher"),
        PRO_VOUCHER_DELETE("Xóa voucher"),

        PRO_GALLERY_READ("Xem thư viện ảnh");

        PRODUCT_ACTION(String label) {
            this.label = label;
        }

        private final String label;

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
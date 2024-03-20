package com.flowiee.dms.utils;

import com.flowiee.dms.core.exception.AppException;
import com.flowiee.dms.core.exception.AuthenticationException;
import com.flowiee.dms.model.MODULE;
import com.flowiee.dms.model.UserPrincipal;
import net.logstash.logback.encoder.org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.File;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class CommonUtils {
    public static String rootPath = "src/main/resources/static";
    public static String fileUploadPath = rootPath + "/uploads/";
    public static String ADMIN = "admin";
    public static Date START_APP_TIME = null;

    public static String getCategoryType(String key) {
        Map<String, String> map = new HashMap<>();
        for (AppConstants.CATEGORY c : AppConstants.CATEGORY.values()) {
            map.put(c.getKey(), c.getName());
        }
        return map.get(key);
    }

    public static String getExtension(String fileName) {
        String extension = "";
        if (ObjectUtils.isNotEmpty(fileName)) {
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0 && lastIndex < fileName.length() - 1) {
                extension = fileName.substring(lastIndex + 1);
            }
        }
        return extension;
    }

    public static String getPathDirectory(String systemModule) {
        try {
            StringBuilder path = new StringBuilder(fileUploadPath);
            if (MODULE.STORAGE.name().equals(systemModule)) {
                path.append("storage");
            } else if (MODULE.CATEGORY.name().equals(systemModule)) {
                path.append("category");
            }
            path.append("/" + DateUtils.getCurrentYear());
            path.append("/" + DateUtils.getCurrentMonth());
            path.append("/" + DateUtils.getCurrentDay());
            File folder = new File(path.toString());
            if (!folder.exists()) {
                if (folder.mkdirs()) {
                    System.out.println("mkdirs OK");
                }
            }
            return path.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static String generateAliasName(String text) {
        String transformedText = "";
        if (text != null) {
            // Loại bỏ dấu tiếng Việt và ký tự đặc biệt
            String normalizedText = Normalizer.normalize(text, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            String textWithoutAccents = pattern.matcher(normalizedText).replaceAll("");
            String cleanedText = textWithoutAccents.replaceAll("[^a-zA-Z0-9 ]", "");

            // Chuyển đổi thành chữ thường (lowercase)
            String lowercaseText = cleanedText.toLowerCase();

            // Thay thế khoảng trắng bằng dấu gạch ngang ("-")
            transformedText = lowercaseText.replace(" ", "-");

            if (transformedText.endsWith("-")) {
                transformedText = transformedText.substring(0, transformedText.length() - 1);
            }
        }
        return transformedText;
    }

    public static int getIdFromAliasPath(String alias) {
        return Integer.parseInt(alias.substring(alias.lastIndexOf("-") + 1));
    }

    public static String getAliasNameFromAliasPath(String alias) {
        return alias.substring(0, alias.lastIndexOf("-"));
    }

    public static UserPrincipal getUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        throw new AuthenticationException();
    }
}
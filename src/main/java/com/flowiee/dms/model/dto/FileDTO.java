package com.flowiee.dms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flowiee.dms.entity.FileStorage;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class FileDTO implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer documentId;
    private Integer orderId;
    private Integer sort;
    private String name;
    private String storageName;
    private String originalName;
    private String extension;
    private String contentType;
    private String module;
    private String note;
    private String uploadBy;
    private String src;
    private Boolean isActive;
    private Boolean status;
    private long size;
    private byte[] content;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private Date uploadAt;

    public static FileDTO fromFileStorage(FileStorage fileStorage) {
        FileDTO dto = new FileDTO();
        if (ObjectUtils.isNotEmpty(fileStorage)) {
            dto.setId(fileStorage.getId());
            if (ObjectUtils.isNotEmpty(fileStorage.getDocument())) {
                dto.setDocumentId(fileStorage.getDocument().getId());
            }
            dto.setSort(fileStorage.getSort());
            dto.setName(fileStorage.getCustomizeName());
            dto.setStorageName(fileStorage.getStorageName());
            dto.setOriginalName(fileStorage.getOriginalName());
            dto.setExtension(fileStorage.getExtension());
            dto.setContentType(fileStorage.getContentType());
            dto.setModule(fileStorage.getModule());
            dto.setNote(fileStorage.getNote());
            dto.setUploadBy(fileStorage.getAccount().getUsername());
            dto.setSrc(fileStorage.getDirectoryPath() + "/" + fileStorage.getStorageName());
            dto.setIsActive(fileStorage.isActive());
            dto.setStatus(fileStorage.isStatus());
            dto.setSize(fileStorage.getFileSize());
            dto.setContent(fileStorage.getContent());
            dto.setUploadAt(fileStorage.getCreatedAt());
        }
        return dto;
    }

    public static List<FileDTO> fromFileStorages(List<FileStorage> fileStorages) {
        List<FileDTO> list = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(fileStorages)) {
            for (FileStorage f : fileStorages) {
                list.add(FileDTO.fromFileStorage(f));
            }
        }
        return list;
    }
}
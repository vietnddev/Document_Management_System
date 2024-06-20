package com.flowiee.dms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.dms.entity.storage.Document;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DocumentDTO extends Document implements Serializable {
    Integer docTypeId;
    String docTypeName;
    FileDTO file;
    @JsonIgnore
    MultipartFile fileUpload;
    String hasSubFolder;
    List<DocumentDTO> subFolders;
    String sharedBy;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    Date sharedAt;
    Boolean thisAccCanUpdate = false;
    Boolean thisAccCanDelete = false;
    Boolean thisAccCanMove = false;
    Boolean thisAccCanShare = false;
    String path;

    public static DocumentDTO fromDocument(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setAsName(document.getAsName());
        dto.setDescription(document.getDescription() != null ? document.getDescription() : "-");
        dto.setParentId(document.getParentId());
        if (document.getDocType() != null) {
            dto.setDocTypeId(document.getDocType().getId());
            dto.setDocTypeName(document.getDocType().getName() != null ? document.getDocType().getName() : "-");
        } else {
            dto.setDocTypeName("-");
        }
        dto.setIsFolder(document.getIsFolder());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setLastUpdatedAt(document.getLastUpdatedAt());
        dto.setLastUpdatedBy(document.getLastUpdatedBy());
        dto.setListDocFile(document.getListDocFile());
        dto.setListDocData(document.getListDocData());
        dto.setListDocShare(document.getListDocShare());
        dto.setListDocHistory(document.getListDocHistory());
        return dto;
    }

    public static List<DocumentDTO> fromDocuments(List<Document> documents) {
        List<DocumentDTO> list = new ArrayList<>();
        if (documents != null) {
            for (Document d : documents) {
                list.add(DocumentDTO.fromDocument(d));
            }
        }
        return list;
    }
}
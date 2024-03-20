package com.flowiee.dms.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.dms.entity.Document;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class DocumentDTO extends Document implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer docTypeId;
    private String docTypeName;
    private FileDTO file;
    @JsonIgnore
    private MultipartFile fileUpload;
    private String hasSubFolder;
    private List<DocumentDTO> subFolders;
    private String sharedBy;
    @JsonFormat(pattern = "HH:mm:ss dd/MM/yyyy")
    private Date sharedAt;
    private Boolean thisAccCanUpdate = false;
    private Boolean thisAccCanDelete = false;
    private Boolean thisAccCanMove = false;
    private Boolean thisAccCanShare = false;

    public static DocumentDTO fromDocument(Document document) {
        DocumentDTO dto = new DocumentDTO();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setAsName(document.getAsName());
        dto.setDescription(document.getDescription());
        dto.setParentId(document.getParentId());
        if (document.getDocType() != null) {
            dto.setDocTypeId(document.getDocType().getId());
            dto.setDocTypeName(document.getDocType().getName() != null ? document.getDocType().getName() : null);
        }
        dto.setIsFolder(document.getIsFolder());
        dto.setCreatedAt(document.getCreatedAt());
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
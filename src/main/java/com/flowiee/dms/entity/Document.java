package com.flowiee.dms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.core.BaseEntity;
import com.flowiee.dms.model.dto.DocumentDTO;
import javax.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Entity
@Table(name = "stg_document")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Document extends BaseEntity implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    @Column(name = "parent_id", nullable = false)
    private Integer parentId;

    @Column(name = "is_folder", nullable = false)
    private String isFolder;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "as_name", nullable = false)
    private String asName;

    @Column(name = "description")
    private String description;

    @JsonIgnore
    @JsonIgnoreProperties("listDocument")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id")
    private Category docType;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocData> listDocData;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileStorage> listDocFile;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocShare> listDocShare;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    private List<DocHistory> listDocHistory;

    public Document(Integer id) {
    	super.id = id;
    }
    
    public Document(Integer id, String name) {
    	this.name = name;
    }

    public static Document fromDocumentDTO(DocumentDTO dto) {
        Document document = new Document();
        document.setId(dto.getId());
        document.setParentId(dto.getParentId());
        document.setIsFolder(dto.getIsFolder());
        document.setName(dto.getName());
        document.setAsName(dto.getAsName());
        document.setParentId(dto.getParentId());
        document.setDescription(dto.getDescription());
        if (dto.getDocTypeId() != null) {
            document.setDocType(new Category(dto.getDocTypeId(), dto.getDocTypeName()));
        }
        return document;
    }

    public Map<String, String> compareTo(Document documentToCompare) {
        Map<String, String> map = new HashMap<>();
        if (!this.getParentId().equals(documentToCompare.getParentId())) {
            map.put("Move", "From " +this.getParentId() + "#To " + documentToCompare.getParentId());
        }
        if (!this.getName().equals(documentToCompare.getName())) {
            map.put("Document name", this.getName() + "#" + documentToCompare.getName());
        }
        if (!this.getDocType().getName().equals(documentToCompare.getDocType().getName())) {
            map.put("Document type", this.getDocType().getName() + "#" + documentToCompare.getDocType().getName());
        }
        if (!this.getDescription().equals(documentToCompare.getDescription())) {
            String descriptionOld = this.getDescription().length() > 9999 ? this.getDescription().substring(0, 9999) : this.getDescription();
            String descriptionNew = documentToCompare.getDescription().length() > 9999 ? documentToCompare.getDescription().substring(0, 9999) : documentToCompare.getDescription();
            map.put("Description", descriptionOld + "#" + descriptionNew);
        }
        return map;
    }

	@Override
	public String toString() {
		return "Document [id=" + super.id + ", parentId=" + parentId + ", loai=" + isFolder + ", ten=" + name + ", aliasName=" + asName
				+ ", moTa=" + description + ", loaiTaiLieu=" + docType + "]";
	}        
}
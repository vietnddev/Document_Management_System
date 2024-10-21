package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import com.flowiee.dms.entity.category.Category;
import com.flowiee.dms.model.dto.DocumentDTO;
import javax.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Builder
@Entity
@Table(name = "document")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Document extends BaseEntity implements Serializable {
    @Column(name = "parent_id", nullable = false)
    Long parentId;

    @Column(name = "is_folder", nullable = false)
    String isFolder;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "as_name", nullable = false)
    String asName;

    @Column(name = "description")
    String description;

    @JsonIgnore
    @JsonIgnoreProperties("listDocument")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id")
    Category docType;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DocData> listDocData;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<FileStorage> listDocFile;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<DocShare> listDocShare;

    @JsonIgnore
    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    List<DocHistory> listDocHistory;

    public Document(Long id) {
    	super.id = id;
    }
    
    public Document(Long id, String name) {
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

    public boolean isFile() {
        return Objects.equals(isFolder, "N");
    }

	@Override
	public String toString() {
		return "Document {id=" + super.id + ", parentId=" + parentId + ", isFolder=" + isFolder + ", name=" + name + ", aliasName=" + asName
				+ ", moTa=" + description + "}";
	}
}
package com.flowiee.dms.entity.storage.view;

import com.flowiee.dms.base.BaseEntity;
import com.flowiee.dms.model.dto.DocumentDTO;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "document_tree_view")
public class DocumentTreeView extends BaseEntity {
    @Column(name = "parent_id")
    Integer parentId;

    @Column(name = "is_folder")
    String isFolder;

    String name;

    @Column(name = "as_name", nullable = false)
    String asName;

    String description;

    @Column(name = "doc_type_id")
    Integer documentTypeId;

    @Column(name = "Has_SubFolders")
    String hasSubFolders;

    @Column(name = "SubFoldersId")
    String subFoldersId;

    @Column(name = "HierarchyLevel")
    String hierarchyLevel;

    String rowNumm;

    String path;

    public static DocumentDTO toDocDTO(DocumentTreeView docTreeView) {
        DocumentDTO docDTO = new DocumentDTO();
        docDTO.setId(docTreeView.getId());
        docDTO.setName(docTreeView.getName());
        docDTO.setAsName(docTreeView.getAsName());
        docDTO.setParentId(docTreeView.getParentId());
        docDTO.setHasSubFolder(docTreeView.getHasSubFolders());
        docDTO.setPath(docTreeView.getPath());
        docDTO.setCreatedAt(docTreeView.getCreatedAt());
        docDTO.setCreatedBy(docTreeView.getCreatedBy());
        return docDTO;
    }
}
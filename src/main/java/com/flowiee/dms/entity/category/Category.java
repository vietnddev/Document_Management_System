package com.flowiee.dms.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.entity.storage.DocField;
import com.flowiee.dms.entity.storage.Document;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "category")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Category extends BaseEntity implements Serializable {
	@Column(name = "type", length = 20, nullable = false)
	String type;

	@Column(name = "code", length = 20, columnDefinition = "VARCHAR2(20) DEFAULT ''")
	String code;

	@Column(name = "name", length = 50, nullable = false)
	String name;

	@Column(name = "sort")
	Integer sort;

	@Column(name = "icon")
	String icon;

	@Column(name = "color", length = 20)
	String color;

	@Column(name = "parent_id")
	Integer parentId;

	@Column(name = "note", length = 500)
	String note;

	@Column(name = "endpoint", length = 50)
	String endpoint;

	@Column(name = "is_default", length = 1, nullable = false)
	String isDefault;

	@Column(name = "status", length = 20, nullable = false)
	Boolean status;

	@Transient
	Integer totalSubRecords;

	@JsonIgnore
	@OneToMany(mappedBy = "docType", fetch = FetchType.LAZY)
	List<Document> listDocument;

	@JsonIgnore
	@OneToMany(mappedBy = "docType", fetch = FetchType.LAZY)
	List<DocField> listDocfield;

	@JsonIgnore
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	List<CategoryHistory> listCategoryHistory;

	public Category(Long id, String name) {
		super.id = id;
		this.name = name;
	}

	public Map<String, String> compareTo(Category categoryToCompare) {
		Map<String, String> map = new HashMap<>();
		if (!this.getCode().equals(categoryToCompare.getCode())) {
			map.put("CODE", this.getCode() + "#" + categoryToCompare.getCode());
		}
		if (!this.getName().equals(categoryToCompare.getName())) {
			map.put("NAME", this.getName() + "#" + categoryToCompare.getName());
		}
		return map;
	}

	@Override
	public String toString() {
		return "Category [id= " + super.id + ", type=" + type + ", code=" + code + ", name=" + name + ", sort=" + sort + ", color=" + color
				+ ", note=" + note + ", endpoint=" + endpoint + ", isDefault=" + isDefault + ", status=" + status + "]";
	}
}
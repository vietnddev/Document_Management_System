package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "storage_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StorageHistory extends BaseEntity implements Serializable {
    public static String EMPTY = "-";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_data_id")
    DocData docData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_storage_id")
    FileStorage fileStorage;

    @Column(name = "title", nullable = false)
    String title;

    @Column(name = "field_name", nullable = false)
    String fieldName;

    @Lob
    @Column(name = "old_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    String newValue;

	@Override
	public String toString() {
		return "DocHistory [id=" + super.id + ", document=" + document + ", docData=" + docData + ", fileStorage=" + fileStorage + ", title="
				+ title + ", fieldName=" + fieldName + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
	}        
}
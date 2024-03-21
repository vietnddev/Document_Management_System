package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Entity
@Table(name = "stg_doc_data")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocData extends BaseEntity implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "value", length = 2000)
    private String value;

    @JsonIgnoreProperties("listDocData")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_field_id", nullable = false)
    private DocField docField;

    @JsonIgnoreProperties("listDocData")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @OneToMany(mappedBy = "docData", fetch = FetchType.LAZY)
    private List<DocHistory> listDocHistory;

    public Map<String, String> compareTo(DocData entityToCompare) {
        Map<String, String> map = new HashMap<>();
        if (!this.getValue().equals(entityToCompare.getValue())) {
            map.put("DocData - " + this.getDocField().getName(), this.getValue() + "#" + entityToCompare.getValue());
        }
        return map;
    }

	@Override
	public String toString() {
		return "DocData [id=" + super.id + ", content=" + value + ", docField=" + docField + ", document=" + document + "]";
	}        
}
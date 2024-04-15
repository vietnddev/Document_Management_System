package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.entity.category.Category;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "doc_field")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocField extends BaseEntity implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "min_length", nullable = false)
    private Integer minLength;

    @Column(name = "max_length", nullable = false)
    private Integer maxLength;

    @Column(name = "min_number", nullable = false)
    private Integer minNumber;

    @Column(name = "max_number", nullable = false)
    private Integer maxNumber;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Column(name = "sort")
    private Integer sort;

    @Column(name = "status", nullable = false)
    private Boolean status;

    @JsonIgnoreProperties("listDocField")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doc_type_id", nullable = false)
    private Category docType;

    @JsonIgnoreProperties("docField")
    @OneToMany(mappedBy = "docField", fetch = FetchType.LAZY)
    private List<DocData> listDocData;

    public DocField(Integer id) {
    	super.id = id;
    }
}
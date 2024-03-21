package com.flowiee.dms.entity.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "category_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CategoryHistory extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "field", nullable = false)
    private String field;

    @Lob
    @Column(name = "old_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    private String oldValue;

    @Lob
    @Column(name = "new_value", nullable = false, length = 9999, columnDefinition = "CLOB")
    private String newValue;

	@Override
	public String toString() {
		return "CategoryHistory [id=" + super.id + ", category=" + category + ", title=" + title + ", fieldName=" + field + ", oldValue=" + oldValue + ", newValue=" + newValue + "]";
	}
}
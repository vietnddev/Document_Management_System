package com.flowiee.dms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.core.BaseEntity;
import javax.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@Entity
@Table(name = "stg_doc_share")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocShare extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

	@Override
	public String toString() {
		return "DocShare [id=" + super.id + ", document=" + document + ", account=" + account + "]";
	}        
}
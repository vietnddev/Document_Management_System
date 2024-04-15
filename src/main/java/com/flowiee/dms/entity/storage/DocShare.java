package com.flowiee.dms.entity.storage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.entity.system.Account;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Entity
@Table(name = "doc_share")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DocShare extends BaseEntity implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "role", nullable = false)
    private String role;

    public DocShare(Integer documentId, Integer accountId, String role) {
        this.document = new Document(documentId);
        this.account = new Account(accountId);
        this.role = role;
    }

	@Override
	public String toString() {
		return "DocShare [id=" + super.id + ", document=" + document + ", account=" + account + "]";
	}
}
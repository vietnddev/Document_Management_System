package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "account_role")
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRole extends BaseEntity implements java.io.Serializable {
	@Column(name = "module", nullable = false)
	@NotNull
	String module;

	@Column(name = "action", nullable = false)
	@NotNull
	String action;

	@Column(name = "account_id")
	Integer accountId;

	@Column(name = "group_id")
	Integer groupId;

	public AccountRole(String module, String action, Integer accountId, Integer groupId) {
		this.module = module;
		this.action = action;
		this.accountId = accountId;
		this.groupId = groupId;
	}

	@Override
	public String toString() {
		return "AccountRole [module=" + module + ", action=" + action + ", accountId=" + accountId + "]";
	}
}
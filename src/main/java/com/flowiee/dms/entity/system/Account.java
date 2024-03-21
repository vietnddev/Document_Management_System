package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.FileStorage;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "sys_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Account extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "username", nullable = false)
	private String username;

	@JsonIgnore
	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "fullname", nullable = false)
	private String fullName;

	@Column(name = "sex", nullable = false)
	private boolean sex;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "address")
	private String diaChi;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "remark")
	private String remark;

	@Column(name = "role")
	private String role;

	@Column(name = "status")
	private boolean status;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<FileStorage> listFileStorage;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<DocShare> listDocShare;

	@Transient
	private String ip;

	public Account(Integer id) {
		super.id = id;
	}

	public Account(Integer id, String username, String fullName) {
		super.id = id;
		this.username = username;
		this.fullName = fullName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Account [");
		builder.append("username=").append(username);
		builder.append(", fullName=").append(fullName);
		builder.append(", sex=").append(sex);
		builder.append(", phoneNumber=").append(phoneNumber);
		builder.append(", email=").append(email);
		builder.append(", diaChi=").append(diaChi);
		builder.append(", avatar=").append(avatar);
		builder.append(", remark=").append(remark);
		builder.append(", role=").append(role);
		builder.append(", status=").append(status);
		builder.append(", ip=").append(ip);
		builder.append("]");
		return builder.toString();
	}
}
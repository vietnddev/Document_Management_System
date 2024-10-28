package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import com.flowiee.dms.entity.storage.DocShare;
import com.flowiee.dms.entity.storage.FileStorage;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Builder
@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Account extends BaseEntity implements Serializable {
	@Column(name = "username", nullable = false)
	String username;

	@JsonIgnore
	@Column(name = "password", nullable = false, length = 100)
	String password;

	@Column(name = "fullname", nullable = false)
	String fullName;

	@Column(name = "sex", nullable = false)
	boolean sex;

	@Column(name = "phone_number", length = 15)
	String phoneNumber;

	@Column(name = "email", length = 50)
	String email;

	@Column(name = "address", length = 500)
	String diaChi;

	@Column(name = "avatar")
	String avatar;

	@Column(name = "remark", length = 500)
	String remark;

	@Column(name = "role", length = 20)
	String role;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_account")
	GroupAccount groupAccount;

	@Column(name = "reset_tokens", unique = true)
	String resetTokens;

	@Column(name = "status")
	boolean status;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<FileStorage> listFileStorage;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<DocShare> listDocShare;

	@JsonIgnore
	@OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
	List<Notification> listNotify;

	@JsonIgnore
	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	List<SystemLog> listLog;

	@Transient
	String ip;

	public Account(Long id) {
		super.id = id;
	}

	public Account(Long id, String username, String fullName) {
		super.id = id;
		this.username = username;
		this.fullName = fullName;
	}

	public FileStorage getAvatar() {
		if (getListFileStorage() != null) {
			for (FileStorage avatar : getListFileStorage()) {
				if (avatar.isActive())
					return avatar;
			}
		}
		return null;
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
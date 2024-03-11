package com.flowiee.dms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.core.BaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "sys_config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SystemConfig extends BaseEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "code", nullable = false)
	private String code;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "value", length = 1000)
	private String value;

	@Column(name = "sort")
	private Integer sort;
	
	public SystemConfig(Integer id, String key, String name, String value, Integer sort) {
		super.id = id;
		this.code = key;
		this.value = value;
		this.sort = sort;
	}

	@Override
	public String toString() {
		return "SystemConfig [id=" + super.id + ", code=" + code + ", name=" + name + ", value=" + value + ", sort=" + sort + "]";
	}
}
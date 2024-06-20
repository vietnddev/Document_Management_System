package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "config")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemConfig extends BaseEntity implements Serializable {
	@Column(name = "code", nullable = false)
	String code;

	@Column(name = "name", nullable = false)
	String name;

	@Column(name = "value", length = 1000)
	String value;

	@Column(name = "sort")
	Integer sort;
	
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
package com.flowiee.dms.entity;

import com.flowiee.dms.core.BaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "sys_languages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Language extends BaseEntity implements Serializable {
    @Serial
	private static final long serialVersionUID = 1L;

	@Column(name = "code")
    private String code;
    
    @Column(name = "key")
    private String key;
    
    @Column(name = "value")
    private String value;
}
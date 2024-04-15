package com.flowiee.dms.entity.system;

import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "languages")
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
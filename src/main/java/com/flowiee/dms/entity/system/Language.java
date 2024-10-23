package com.flowiee.dms.entity.system;

import com.flowiee.dms.base.BaseEntity;
import javax.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Language extends BaseEntity implements Serializable {
	@Column(name = "code", length = 3)
    String code;
    
    @Column(name = "key")
    String key;
    
    @Column(name = "value")
    String value;
}
package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "group_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GroupAccount extends BaseEntity implements Serializable {
    @Serial
    static final long serialVersionUID = 1L;

    @Column(name = "group_code")
    String groupCode;

    @Column(name = "group_name", nullable = false)
    String groupName;

    @Column(name = "note")
    String note;

    public GroupAccount(Integer id, String groupCode, String groupName) {
        this.id = id;
        this.groupCode = groupCode;
        this.groupName = groupName;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "groupAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    List<Account> listAccount;
}
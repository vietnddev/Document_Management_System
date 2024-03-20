package com.flowiee.dms.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flowiee.dms.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public class Audit {
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "created_at", updatable = false, columnDefinition = "timestamp default current_timestamp")
    @CreatedDate
    private Date createdAt;

    @Column(name = "created_by", updatable = false)
    @CreatedBy
    protected Integer createdBy;

    @JsonIgnore
    @Column(name = "last_updated_at", columnDefinition = "timestamp default current_timestamp")
    @LastModifiedDate
    private Date lastUpdatedAt;

    @JsonIgnore
    @Column(name = "last_updated_by")
    @LastModifiedBy
    private String lastUpdatedBy;

    @JsonIgnore
    @Column(name = "deleted_at")
    private Date deletedAt;

    @JsonIgnore
    @Column(name = "deleted_by")
    private String deletedBy;

    @PreUpdate
    @PrePersist
    public void updateAudit() {
        lastUpdatedAt = new Date();
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (createdBy == null) {
            createdBy = CommonUtils.getUserPrincipal().getId();
        }
        if (lastUpdatedBy == null) {
            lastUpdatedBy = CommonUtils.getUserPrincipal().getUsername();
        }
    }
}
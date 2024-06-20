package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flowiee.dms.base.BaseEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification extends BaseEntity implements Serializable {
    @Column(name = "message", nullable = false)
    String message;

    @Column(name = "doc_shared_id")
    Integer docSharedId;

    @Column(name = "send")
    String send;

    @Column(name = "receive")
    String receive;
}
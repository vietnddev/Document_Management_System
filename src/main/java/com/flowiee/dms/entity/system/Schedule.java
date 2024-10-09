package com.flowiee.dms.entity.system;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Entity
@Table(name = "schedule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Schedule {
    @Id
    @Column(name = "schedule_id", nullable = false)
    private String scheduleId;

    @Column(name = "schedule_name", nullable = false)
    private String scheduleName;

    @Column(name = "enable")
    private boolean enable;

    @JsonIgnore
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    List<ScheduleStatus> listScheduleStatuses;
}
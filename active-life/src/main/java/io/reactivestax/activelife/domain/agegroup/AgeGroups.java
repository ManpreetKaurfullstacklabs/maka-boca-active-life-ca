package io.reactivestax.activelife.domain.agegroup;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "age")
public class AgeGroups {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agegroup_id")
    private Long ageGroupId;

    @Column(name = "age")
    private int shortCode;

    @Column(name = "description")
    private  String description;

    @Column(name = "created_at")
    private LocalDateTime createdTimestamp;

    @Column(name = "lastUpdated_at")
    private  LocalDateTime lastUpLocalDatedTimestamp;

    @Column(name = "created_by")
    private  Long createdBy;

    @Column(name = "lastUpdated_by")
    private Long lastUpLocalDatedBy;
    
}

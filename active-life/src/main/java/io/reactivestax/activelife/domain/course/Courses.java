package io.reactivestax.activelife.domain.course;

import io.reactivestax.activelife.domain.agegroup.AgeGroups;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courses")
public class Courses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private  Subcategories subcategories;

    @ManyToOne
    @JoinColumn(name = "age_group_id")
    private AgeGroups ageGroups;


    @Column(name = "created_at")
    private LocalDate createdTimestamp;

    @Column(name = "lastUpdated_at")
    private LocalDate lastUpdatedTimestamp;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "lastUpdated_by")
    private Long lastUpdatedBy;


}

package io.reactivestax.activelife.domain.membership;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "family_course_registration")
public class FamilyCourseRegistrations {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "family_course_registration_id")
    private Long familyCourseRegistrationId;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name= "isWithdrawn")
    @Enumerated(EnumType.STRING)
    private IsWithdrawn isWithdrawn;

    @Column(name= "withdrawn_credits")
    private Long withdrawnCredits;

    @Column(name="no_of_seats")
    private Long noOfseats;

    @Column(name= "enrollment_actor")
    private String enrollmentActor ;

    @Column(name= "enrollment_actor_id")
    private Long enrollmentActorId;

    @Column(name= "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name="offer_course_id" )
    private OfferedCourses offeredCourseId;

    @ManyToOne
    @JoinColumn(name="family_member_id" )
    private FamilyMembers familyMemberId;

    @Column(name="last_updated_time")
    private LocalDateTime lastUpdatedTime;

    @Column(name="created_by")
    private Long createdBy;

    @Column(name="last_updated_by")
    private Long lastUpdateBy;

    @Column(name="isWaitlisted")
    @Enumerated(EnumType.STRING)
    private IsWaitListed isWaitListed;

}

package io.reactivestax.activelife.domain;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Data
@Entity
@Table(name = "wait_list")
public class WaitList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long waitListId;

    @ManyToOne
    @JoinColumn(name = "offered_course_id")
    private OfferedCourses offeredCourses;

    @ManyToOne
    @JoinColumn(name = "family_member_id")
    private FamilyMembers familyMember;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
}

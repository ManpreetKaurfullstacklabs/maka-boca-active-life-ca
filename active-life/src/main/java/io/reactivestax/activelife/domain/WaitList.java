package io.reactivestax.activelife.domain;
import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.Status;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "waitlist")
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

    @Column(name = "no_of_seats")
    private Long noOfSeats;

    @Column(name="isWaitlisted")
    @Enumerated(EnumType.STRING)
    private IsWaitListed isWaitListed;


}

package io.reactivestax.activelife.repository.familymember;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistrations, Long> {


    Long countByOfferedCourseIdAndIsWithdrawn(OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);
    List<FamilyCourseRegistrations> findByIsWaitListed(IsWaitListed isWaitListed);
    Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourseId(FamilyMembers familyMember, OfferedCourses offeredCourse);
 //   List<FamilyCourseRegistrations> findByFamilyMemberId(Long memberId);
}

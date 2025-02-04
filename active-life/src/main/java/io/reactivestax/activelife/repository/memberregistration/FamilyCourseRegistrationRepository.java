package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistrations, Long> {


    Long countByOfferedCourseIdAndIsWithdrawn(OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);
    Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourseId(MemberRegistration familyMember, OfferedCourses offeredCourse);
}

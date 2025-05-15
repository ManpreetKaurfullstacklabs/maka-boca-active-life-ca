package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistrations, Long> {
    Long countByOfferedCourseIdAndIsWithdrawn(OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);
    Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourseIdAndIsWithdrawn(MemberRegistration familyMember, OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);

    List<FamilyCourseRegistrations> findByEnrollmentActorId(Long enrollmentActorId);

    List<FamilyCourseRegistrations> findByEnrollmentActorIdAndOfferedCourseId(Long enrollmentActorId, OfferedCourses offeredCourseId);

//    List<FamilyCourseRegistrations> findByFamilyMemberId(Long id);
//    Long findByFamilyMemberId(Long familyMemberID);
//   Optional<FamilyCourseRegistrations> findByMemberLogin(String memberLogin);

 //   Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourseIdAndIsWithdrawn(Long familyMemberId, OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);
    //  Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourse(Long familyMemberId, OfferedCourses offeredCourse);
  //  Optional<FamilyCourseRegistrations> findByFamilyMemberIdAndOfferedCourseId(Long familyMemberId, Long offeredCourseId);

}

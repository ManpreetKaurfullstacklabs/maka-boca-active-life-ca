package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.course.WaitList;
import io.reactivestax.activelife.domain.membership.MemberRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WaitlistRepository extends JpaRepository<WaitList, Long> {
    Long countByOfferedCourses_OfferedCourseIdAndIsWaitListed(Long offeredCourseId, IsWaitListed isWaitListed);
    List<WaitList> findByOfferedCourses_OfferedCourseIdAndIsWaitListed(Long offeredCourseId, IsWaitListed isWaitListed);
    List<WaitList> findByIsWaitListed(IsWaitListed isWaitListed);
  //  Optional<WaitList>findByFamilyMember(MemberRegistration memberRegistration);

    Optional<WaitList> findByFamilyMemberAndOfferedCourses(MemberRegistration familyMember, OfferedCourses offeredCourse);

    //  List<WaitList> findByOfferedCourses_OfferedCourseId_FamilyMemberIdAndIsWaitListed(Long offeredCourseId, Long memberId, IsWaitListed isWaitListed);
}

package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.domain.course.WaitList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaitlistRepository extends JpaRepository<WaitList, Long> {
    Long countByOfferedCourses_OfferedCourseIdAndIsWaitListed(Long offeredCourseId, IsWaitListed isWaitListed);
    List<WaitList> findByOfferedCourses_OfferedCourseIdAndIsWaitListed(Long offeredCourseId, IsWaitListed isWaitListed);
    List<WaitList> findByIsWaitListed(IsWaitListed isWaitListed);
}

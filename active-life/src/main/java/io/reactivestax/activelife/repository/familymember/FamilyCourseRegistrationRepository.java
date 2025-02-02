package io.reactivestax.activelife.repository.familymember;

import io.reactivestax.activelife.Enums.IsWaitListed;
import io.reactivestax.activelife.Enums.IsWithdrawn;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistrations, Long> {

    // Query to count the number of enrolled (non-withdrawn) members for a specific course
    Long countByOfferedCourseIdAndIsWithdrawn(OfferedCourses offeredCourse, IsWithdrawn isWithdrawn);

    // Query to count the number of members who are waitlisted for a specific course
    Long countByOfferedCourseIdAndIsWaitListed(OfferedCourses offeredCourse, IsWaitListed isWaitListed);
}

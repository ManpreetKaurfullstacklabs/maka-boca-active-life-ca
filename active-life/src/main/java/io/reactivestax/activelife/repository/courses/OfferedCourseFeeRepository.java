package io.reactivestax.activelife.repository.courses;

import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferedCourseFeeRepository extends JpaRepository<OfferedCourseFee, Long> {
}

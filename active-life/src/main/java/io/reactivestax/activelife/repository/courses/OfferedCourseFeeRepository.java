package io.reactivestax.activelife.repository.courses;

import io.reactivestax.activelife.domain.course.OfferedCourseFee;
import io.reactivestax.activelife.domain.course.OfferedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OfferedCourseFeeRepository extends JpaRepository<OfferedCourseFee, Long> , JpaSpecificationExecutor<OfferedCourses> {



}

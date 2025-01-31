package io.reactivestax.activelife.repository;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferedCourseRepository extends JpaRepository<OfferedCourses,Long> {
}

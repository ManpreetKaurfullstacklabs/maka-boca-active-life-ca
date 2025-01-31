package io.reactivestax.activelife.repository.courses;

import io.reactivestax.activelife.domain.course.Courses;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoursesRepository extends JpaRepository<Courses,Long> {
}

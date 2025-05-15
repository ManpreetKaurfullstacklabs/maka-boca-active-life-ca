package io.reactivestax.activelife.repository.courses;

import io.reactivestax.activelife.domain.course.OfferedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferedCourseRepository extends JpaRepository<OfferedCourses,Long>, JpaSpecificationExecutor<OfferedCourses> {


}

package io.reactivestax.activelife.repository;

import io.reactivestax.activelife.dto.OfferedCoursesDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfferedCoursesRepository extends JpaRepository<OfferedCoursesDTO,Long> {
}

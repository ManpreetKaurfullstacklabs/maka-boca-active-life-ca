package io.reactivestax.activelife.repository.familymemberrepositries;

import io.reactivestax.activelife.domain.membership.FamilyCourseRegistrations;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyCourseRegistrationRepository extends JpaRepository<FamilyCourseRegistrations,Long> {
}

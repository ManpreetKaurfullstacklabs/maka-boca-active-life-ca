package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.domain.membership.FamilyGroups;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyGroupRepository extends JpaRepository<FamilyGroups,Long> {
}

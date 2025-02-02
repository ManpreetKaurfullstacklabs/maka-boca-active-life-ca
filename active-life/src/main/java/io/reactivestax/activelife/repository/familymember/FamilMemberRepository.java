package io.reactivestax.activelife.repository.familymember;

import io.reactivestax.activelife.domain.membership.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FamilMemberRepository extends JpaRepository<FamilyMembers, Long> {
    //Optional<FamilyMembers> findByMemberLogin(Long memberLoginId);
    Optional<FamilyMembers> findByVerificationUUID(String verificationIdId);
    Optional<FamilyMembers> findByMemberLogin(String memberLogin);

}

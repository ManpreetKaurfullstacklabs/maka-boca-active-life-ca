package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.domain.membership.MemberRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRegistrationRepository extends JpaRepository<MemberRegistration, Long> {
    Optional<MemberRegistration> findByVerificationUUID(String verificationIdId);
    Optional<MemberRegistration> findByMemberLogin(String memberLogin);
    Optional<MemberRegistration> findByFamilyMemberId(Long familyMemberId);
}

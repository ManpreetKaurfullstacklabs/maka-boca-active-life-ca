package io.reactivestax.activelife.repository.login;

import io.reactivestax.activelife.controller.MemberRegistration;
import io.reactivestax.activelife.domain.membership.Login;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login,Long> {

    Optional<FamilyMembers> findByVerificationUUID(String verificationIdId);
    Optional<Login> findByFamilyMember(MemberRegistration memberRegistration);
}

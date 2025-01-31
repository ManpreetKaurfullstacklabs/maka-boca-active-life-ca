package io.reactivestax.activelife.repository.login;

import io.reactivestax.activelife.domain.Login;
import io.reactivestax.activelife.domain.membership.FamilyMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Login,Long> {

    Optional<FamilyMembers> findByVerificationUUID(String verificationIdId);

}

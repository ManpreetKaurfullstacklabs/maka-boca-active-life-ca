package io.reactivestax.activelife.repository.memberregistration;

import io.reactivestax.activelife.domain.membership.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Long> { }

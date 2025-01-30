package io.reactivestax.activelife.repository;

import io.reactivestax.activelife.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login,Long> {
}

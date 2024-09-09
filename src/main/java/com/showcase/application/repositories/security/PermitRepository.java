package com.showcase.application.repositories.security;

import com.showcase.application.models.security.Permit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermitRepository extends JpaRepository<Permit, Long>, JpaSpecificationExecutor<Permit> {
    Permit findFirstByEnabledAndCode(boolean enabled, String code);

    List<Permit> findAllByEnabled(boolean enabled);
    List<Permit> findAllByEnabledAndPermitFatherIsNull(boolean enabled);
    List<Permit> findAllByEnabledAndPermitFather(boolean enabled, Permit father);
}

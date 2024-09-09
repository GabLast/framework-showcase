package com.showcase.application.repositories.security;

import com.showcase.application.models.security.Profile;
import com.showcase.application.models.security.ProfilePermit;
import com.showcase.application.models.security.ProfileUser;
import com.showcase.application.models.security.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfilePermitRepository extends JpaRepository<ProfilePermit, Long>, JpaSpecificationExecutor<ProfilePermit> {

    @EntityGraph(attributePaths = {"permit"})
    List<ProfilePermit> findAllByEnabledAndProfileAndPermit_EnabledIsTrueAndProfile_EnabledIsTrue(boolean enabled, Profile profile);

    List<ProfilePermit> findAllByEnabledIsTrue();
}

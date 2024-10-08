package com.showcase.application.repositories.security;

import com.showcase.application.models.security.ProfileUser;
import com.showcase.application.models.security.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileUserRepository extends JpaRepository<ProfileUser, Long>, JpaSpecificationExecutor<ProfileUser> {

    @EntityGraph(attributePaths = {"profile"})
    List<ProfileUser> findAllByEnabledAndUser(boolean enabled, User user);
}

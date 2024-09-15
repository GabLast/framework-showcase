package com.showcase.application.repositories.security;

import com.showcase.application.models.security.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String a);

    User findByMail(String a);

    @Query("select " +
            "user " +
            "from User user " +
            "where (lower(trim(user.username)) like trim(lower(:value)) or (lower(trim(user.mail)) like trim(lower(:value))))"
    )
    User findByUsernameOrMail(@Param("value") String value);

    @Query("select " +
            "u " +
            "from User as u " +
            "where (:admin is null or u.admin = :admin) " +
            "and (:enabled is null or u.enabled = :enabled) " +
            "and (:username is null or u.username like '' or u.username like lower(trim(concat('%', :username,'%')))) " +
            "and (:mail is null or u.mail like '' or u.mail like lower(trim(concat('%', :mail,'%')))) "
    )
    List<User> findAllFilter(@Param("enabled") Boolean enabled,
                             @Param("username") String username,
                             @Param("mail") String mail,
                             @Param("admin") Boolean admin,
                             Pageable pageable
    );

    @Query("select " +
            "count(u) " +
            "from User as u " +
            "where (:admin is null or u.admin = :admin) " +
            "and (:enabled is null or u.enabled = :enabled) " +
            "and (:username is null or u.username like '' or u.username like lower(trim(concat('%', :username,'%')))) " +
            "and (:mail is null or u.mail like '' or u.mail like lower(trim(concat('%', :mail,'%')))) "
    )
    Integer countAllFilter(@Param("enabled") Boolean enabled,
                           @Param("username") String username,
                           @Param("mail") String mail,
                           @Param("admin") Boolean admin
    );

    List<User> findAllByEnabled(boolean enabled);
}

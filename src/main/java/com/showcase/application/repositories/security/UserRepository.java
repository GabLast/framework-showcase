package com.showcase.application.repositories.security;

import com.showcase.application.models.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
            "where u.enabled = :enabled " +
            "and (:username is null or u.username like '' or u.username like lower(trim(concat('%', :username,'%')))) " +
            "and (:mail is null or u.mail like '' or u.mail like lower(trim(concat('%', :mail,'%')))) "
    )
    List<User> findAllFilter(@Param("username") String username,
                             @Param("mail") String mail,
                             @Param("enabled") boolean enabled
    );

    @Query("select " +
            "count(distinct u) " +
            "from User as u " +
            "where u.enabled = :enabled " +
            "and (:username is null or u.username like '' or u.username like lower(trim(concat('%', :username,'%')))) " +
            "and (:mail is null or u.mail like '' or u.mail like lower(trim(concat('%', :mail,'%')))) "
    )
    Integer countAllFilter(@Param("username") String username,
                           @Param("mail") String mail,
                           @Param("enabled") boolean enabled
    );

    List<User> findAllByEnabled(boolean enabled);
}

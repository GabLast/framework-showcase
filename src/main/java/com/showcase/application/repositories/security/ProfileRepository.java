package com.showcase.application.repositories.security;

import com.showcase.application.models.security.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("select " +
            "u " +
            "from Profile as u " +
            "where (:enabled is null or u.enabled = :enabled) " +
            "and (:name is null or u.name like '' or u.name like lower(trim(concat('%', :name,'%')))) " +
            "and (:description is null or u.description like '' or u.description like lower(trim(concat('%', :description,'%')))) "
    )
    List<Profile> findAllFilter(@Param("enabled") Boolean enabled,
                                @Param("name") String name,
                                @Param("description") String description,
                                Pageable pageable
    );

    @Query("select " +
            "count(u) " +
            "from Profile as u " +
            "where (:enabled is null or u.enabled = :enabled) " +
            "and (:name is null or u.name like '' or u.name like lower(trim(concat('%', :name,'%')))) " +
            "and (:description is null or u.description like '' or u.description like lower(trim(concat('%', :description,'%')))) "
    )
    Integer countAllFilter(@Param("enabled") Boolean enabled,
                           @Param("name") String name,
                           @Param("description") String description
    );


    List<Profile> findAllByEnabled(boolean enabled);
}

package com.showcase.application.repositories.module;

import com.showcase.application.models.module.TestData;
import com.showcase.application.models.module.TestType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {

    @EntityGraph(attributePaths = {"testType"})
    Optional<TestData> getTestDataById(Long id);


    @EntityGraph(attributePaths = {"testType"})
    @Query("select " +
            "u " +
            "from TestData as u " +
            "where u.enabled = :enabled " +
            "and (:word is null or u.word like '' or lower(u.word) like lower(trim(concat('%', :word,'%')))) " +
            "and (:description is null or u.description like '' or u.description is null or lower(u.description) like lower(trim(concat('%', :description,'%')))) " +
            "and (:testType is null or u.testType = :testType) " +
            "and (:start is null or u.date >= :start) " +
            "and (:end is null or u.date <= :end) "
    )
    List<TestData> findAllFilter(@Param("enabled") boolean enabled,
                                 @Param("word") String word,
                                 @Param("description") String description,
                                 @Param("testType") TestType testType,
                                 @Param("start") Date start,
                                 @Param("end") Date end,
                                 Pageable pageable
    );

    @Query("select " +
            "count (u) " +
            "from TestData as u " +
            "where u.enabled = :enabled " +
            "and (:word is null or u.word like '' or lower(u.word) like lower(trim(concat('%', :word,'%')))) " +
            "and (:description is null or u.description like '' or u.description is null or lower(u.description) like lower(trim(concat('%', :description,'%')))) " +
            "and (:testType is null or u.testType = :testType) " +
            "and (:start is null or u.date >= :start) " +
            "and (:end is null or u.date <= :end) "
    )
    Integer countAllFilter(@Param("enabled") boolean enabled,
                           @Param("word") String word,
                           @Param("description") String description,
                           @Param("testType") TestType testType,
                           @Param("start") Date start,
                           @Param("end") Date end
    );


}

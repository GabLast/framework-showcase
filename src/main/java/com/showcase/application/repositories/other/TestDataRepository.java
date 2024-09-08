package com.showcase.application.repositories.other;

import com.showcase.application.models.other.TestData;
import com.showcase.application.models.other.TestType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TestDataRepository extends JpaRepository<TestData, Long> {

    @EntityGraph(attributePaths = {"testType"})
    @Query("select " +
            "u " +
            "from TestData as u " +
            "where u.enabled = :enabled " +
            "and (:word is null or u.word like '' or u.word like lower(trim(concat('%', :word,'%')))) " +
            "and (:description is null or u.description like '' or u.description like lower(trim(concat('%', cast(:description as string),'%')))) " +
            "and (:testType is null or u.testType = :testType) " +
            "and (:start is null or u.lastUpdated >= :start) " +
            "and (:end is null or u.lastUpdated <= :end) "
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
            "and (:word is null or u.word like '' or u.word like lower(trim(concat('%', :word,'%')))) " +
            "and (:description is null or u.description like '' or u.description like lower(trim(concat('%', cast(:description as string ) ,'%')))) " +
            "and (:testType is null or u.testType = :testType) " +
            "and (:start is null or u.lastUpdated >= :start) " +
            "and (:end is null or u.lastUpdated <= :end) "
    )
    Integer countAllFilter(@Param("enabled") boolean enabled,
                           @Param("word") String word,
                           @Param("description") String description,
                           @Param("testType") TestType testType,
                           @Param("start") Date start,
                           @Param("end") Date end
    );
}

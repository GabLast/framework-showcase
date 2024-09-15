package com.showcase.application.repositories.configuration;

import com.showcase.application.models.configuration.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends JpaRepository<Parameter, Long>, JpaSpecificationExecutor<Parameter> {

    Parameter findFirstByEnabledAndCode(boolean enabled, String code);
}

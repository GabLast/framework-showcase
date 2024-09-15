package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.module.TestType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestTypeRest {

    public TestTypeRest(TestType testType) {
        this.id = testType.getId();
        this.code = testType.getCode();
        this.name = TestType.toStringI18nKey(testType);
        this.description = testType.getDescription();
    }

    private Long id;
    private Integer code;
    private String name;
    private String description;
}

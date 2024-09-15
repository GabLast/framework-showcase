package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.rest.RestBase;
import com.showcase.application.models.rest.RestPagination;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterTestData extends RestBase {

    private String word;
    private String description;
    private TestTypeRest testTypeRest;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private RestPagination restPagination;

    public TestType getTestType(TestTypeRest testTypeRest) {
        return new TestType(testTypeRest);
    }
}

package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.module.TestType;
import com.showcase.application.models.rest.RestPagination;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@ToString
public class FilterTestData extends RestPagination {

    private String word = null;
    private String description = null;
    private Long testType = null;
    private LocalDate dateStart = null;
    private LocalDate dateEnd = null;
    //    private RestPagination restPagination = new RestPagination();
    private Integer reportType;

    public TestType getTestType(TestTypeRest testTypeRest) {
        if (testTypeRest == null) {
            return null;
        }
        return new TestType(testTypeRest);
    }

}

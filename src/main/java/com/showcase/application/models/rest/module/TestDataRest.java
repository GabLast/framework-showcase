package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.module.TestData;
import com.showcase.application.models.rest.RestBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TestDataRest extends RestBase {

    public TestDataRest(TestData testData) {
        this.id = testData.getId();
        this.word = testData.getWord();
        this.date = testData.getDate();
        this.testTypeRest = new TestTypeRest(testData.getTestType());
        this.description = testData.getDescription();
    }

    private Long id;
    private String word;
    private Date date;
    private TestTypeRest testTypeRest;
    private String description;
}

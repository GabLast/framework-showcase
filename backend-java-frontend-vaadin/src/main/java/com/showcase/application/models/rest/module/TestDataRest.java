package com.showcase.application.models.rest.module;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.module.TestData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TestDataRest {

    public TestDataRest(TestData testData) {
        this.id = testData.getId();
        this.word = testData.getWord();
        this.date = testData.getDate();
        this.testTypeRest = new TestTypeRest(testData.getTestType());
        this.description = testData.getDescription();
        this.number = testData.getNumber();
    }

    private Long id;
    private String word;
    private Date date;
    private TestTypeRest testTypeRest;
    private String description;
    private BigDecimal number;
}

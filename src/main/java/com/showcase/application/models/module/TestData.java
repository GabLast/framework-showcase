package com.showcase.application.models.module;


import com.showcase.application.models.Base;
import com.showcase.application.models.rest.module.TestDataRest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
//@Audited
//@AuditOverride(forClass = Base.class)
public class TestData extends Base {

    @Column(nullable = false)
    private String word;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    private TestType testType;

    @Column(columnDefinition = "longtext")
    private String description;

    private BigDecimal number;

    public String toString() {
        return word;
    }

    public TestData(TestDataRest testDataRest) {
        this.word = testDataRest.getWord();
        this.description = testDataRest.getDescription();
        this.testType = new TestType(testDataRest.getTestTypeRest());
        this.word = testDataRest.getWord();
    }
}

package com.showcase.application.models.module;


import com.showcase.application.models.Base;
import com.showcase.application.models.rest.dto.TestDataDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
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
    @Transient
    private Long testTypeId;

    @Column(columnDefinition = "longtext")
    private String description;

    private BigDecimal number;

    public String toString() {
        return word;
    }

    public TestData(TestDataDto testDataDto) {
        this.id = testDataDto.getId();
        this.word = testDataDto.getWord();
        this.description = testDataDto.getDescription();
        this.testTypeId = testDataDto.getTestTypeId();
        this.date = testDataDto.getDate();
        this.number = testDataDto.getNumber();
    }
}

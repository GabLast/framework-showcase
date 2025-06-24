package com.showcase.application.models.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.RestBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TestDataDto extends RestBase {

    private Long id;
    private String word;
    private Date date;
    private Long testTypeId;
    private String description;
    private BigDecimal number;

}

package com.showcase.application.models.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.RestBase;
import com.showcase.application.models.rest.module.TestTypeRest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class TestDataDto extends RestBase {

    private Long id;
    private String word;
    private Date date;
    private TestTypeRest testTypeRest;
    private String description;

}

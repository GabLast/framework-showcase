package com.showcase.application.simplerestclient.dto.request.testdata;

import com.showcase.application.simplerestclient.dto.JsonBase;

import java.math.BigDecimal;
import java.util.Date;

public record TestData(
        Long id,
        String word,
        Date date,
        TestTypeRest testTypeRest,
        String description,
        BigDecimal number) implements JsonBase {
}

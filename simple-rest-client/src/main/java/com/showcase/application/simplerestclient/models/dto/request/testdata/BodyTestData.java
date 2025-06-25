package com.showcase.application.simplerestclient.models.dto.request.testdata;

import com.showcase.application.simplerestclient.models.dto.request.BaseRequestBody;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Date;

@Builder
public record BodyTestData(Long id,
                           String word,
                           Long testTypeId,
                           Date date,
                           BigDecimal number) implements BaseRequestBody {
}

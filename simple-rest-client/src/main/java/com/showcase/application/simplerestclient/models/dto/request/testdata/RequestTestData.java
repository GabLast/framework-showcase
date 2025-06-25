package com.showcase.application.simplerestclient.models.dto.request.testdata;

import com.showcase.application.simplerestclient.models.dto.request.BaseHeader;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequest;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestBody;
import lombok.Builder;

import java.util.Objects;

@Builder
public record RequestTestData(BodyTestData bodyTestData, BaseHeader baseHeader, String bearerToken) implements BaseRequest<BaseRequestBody, BaseHeader, String> {

    @Override
    public BaseRequestBody getBody() {
        return bodyTestData;
    }

    @Override
    public BaseHeader getHeaders() {
        return Objects.isNull(baseHeader) ? BaseHeader.builder().build() : baseHeader;
    }

    @Override
    public String getBearer() {
        return bearerToken;
    }
}

package com.showcase.application.simplerestclient.models.dto.request.testdata;

import com.showcase.application.simplerestclient.models.dto.request.BaseHeader;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestGet;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestParams;
import lombok.Builder;

import java.util.Objects;

@Builder
public record RequestTestDataFindAll(
        BaseRequestParams baseRequestParams,
        BaseHeader baseHeader,
        String bearerToken) implements BaseRequestGet<BaseRequestParams, BaseHeader, String> {

    @Override
    public BaseRequestParams getParams() {
        return baseRequestParams;
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

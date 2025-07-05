package com.showcase.application.simplerestclient.dto.request.authentication;

import com.showcase.application.simplerestclient.dto.request.BaseHeader;
import com.showcase.application.simplerestclient.dto.request.BaseRequest;
import com.showcase.application.simplerestclient.dto.request.BaseRequestBody;
import lombok.Builder;

@Builder
public record RequestLogin(BodyLogin body) implements BaseRequest<BaseRequestBody, BaseHeader, String> {

    @Override
    public BaseRequestBody getBody() {
        return body;
    }

    @Override
    public BaseHeader getHeaders() {
        return BaseHeader.builder().build();
    }

    @Override
    public String getBearer() {
        return "";
    }
}

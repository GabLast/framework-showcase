package com.showcase.application.simplerestclient.dto.response.authentication;

import com.showcase.application.simplerestclient.dto.JsonBase;
import com.showcase.application.simplerestclient.dto.ResponseFrame;
import com.showcase.application.simplerestclient.dto.response.BaseResponse;
import com.showcase.application.simplerestclient.dto.security.UserRest;
import lombok.Builder;

@Builder
public record LoginResponse(
        ResponseFrame responseFrame,
        UserRest userRest,
        String jwt
) implements BaseResponse, JsonBase {
}

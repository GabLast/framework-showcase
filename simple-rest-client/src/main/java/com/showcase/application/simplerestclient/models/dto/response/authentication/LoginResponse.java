package com.showcase.application.simplerestclient.models.dto.response.authentication;

import com.showcase.application.simplerestclient.models.dto.JsonBase;
import com.showcase.application.simplerestclient.models.dto.RequestFrame;
import com.showcase.application.simplerestclient.models.dto.response.BaseResponse;
import com.showcase.application.simplerestclient.models.dto.security.UserRest;
import lombok.Builder;

@Builder
public record LoginResponse(
        RequestFrame requestFrame,
        UserRest userRest,
        String jwt
) implements BaseResponse, JsonBase {
}

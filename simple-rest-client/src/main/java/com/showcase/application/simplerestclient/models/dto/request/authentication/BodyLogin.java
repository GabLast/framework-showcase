package com.showcase.application.simplerestclient.models.dto.request.authentication;

import com.showcase.application.simplerestclient.models.dto.request.BaseRequestBody;
import lombok.Builder;

@Builder
public record BodyLogin(String username, String password) implements BaseRequestBody {
}

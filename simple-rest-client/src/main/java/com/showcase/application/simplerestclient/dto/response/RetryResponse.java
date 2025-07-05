package com.showcase.application.simplerestclient.dto.response;

import com.showcase.application.simplerestclient.dto.JsonBase;

public record RetryResponse<T>(T response) implements JsonBase {
}

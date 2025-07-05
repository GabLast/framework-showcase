package com.showcase.application.simplerestclient.dto.response;

import com.showcase.application.simplerestclient.dto.JsonBase;

public record StandardResponse<T>(T response) implements JsonBase {
}

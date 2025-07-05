package com.showcase.application.simplerestclient.models.dto.response;

import com.showcase.application.simplerestclient.models.dto.JsonBase;

public record StandardResponse<T>(T response) implements JsonBase {
}

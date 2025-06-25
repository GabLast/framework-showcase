package com.showcase.application.simplerestclient.models.dto.security;

import com.showcase.application.simplerestclient.models.dto.JsonBase;

public record UserRest(
        Long id,
        String username,
        String name) implements JsonBase {
}

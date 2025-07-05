package com.showcase.application.simplerestclient.dto.security;

import com.showcase.application.simplerestclient.dto.JsonBase;

public record UserRest(
        Long id,
        String username,
        String name) implements JsonBase {
}

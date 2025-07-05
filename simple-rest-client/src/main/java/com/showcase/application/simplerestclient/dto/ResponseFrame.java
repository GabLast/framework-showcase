package com.showcase.application.simplerestclient.dto;

public record ResponseFrame(
        Integer code,
        String message,
        Boolean error) implements JsonBase {
}

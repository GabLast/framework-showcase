package com.showcase.application.simplerestclient.models.dto;

public record RequestFrame(
        Integer code,
        String message,
        Boolean error) implements JsonBase {

}

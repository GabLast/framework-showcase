package com.showcase.application.simplerestclient.models.dto.request;

import lombok.Builder;

import java.util.HashMap;

@Builder
public record BaseHeader(HashMap<String, String> headers) {
}

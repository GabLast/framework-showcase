package com.showcase.application.simplerestclient.dto.request;

import lombok.Builder;

import java.util.HashMap;

@Builder
public record BaseRequestParams(HashMap<String, String> params) {
}

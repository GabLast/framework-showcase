package com.showcase.application.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record StandardResponse(
        String message,
        String path,
        int status
) {
}
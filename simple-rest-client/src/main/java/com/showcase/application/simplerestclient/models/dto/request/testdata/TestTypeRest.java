package com.showcase.application.simplerestclient.models.dto.request.testdata;

import com.showcase.application.simplerestclient.models.dto.JsonBase;

public record TestTypeRest(Long id,
                           Integer code,
                           String name,
                           String description) implements JsonBase {
}

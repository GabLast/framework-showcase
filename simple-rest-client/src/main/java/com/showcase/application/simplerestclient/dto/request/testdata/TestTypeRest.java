package com.showcase.application.simplerestclient.dto.request.testdata;

import com.showcase.application.simplerestclient.dto.JsonBase;

public record TestTypeRest(Long id,
                           Integer code,
                           String name,
                           String description) implements JsonBase {
}

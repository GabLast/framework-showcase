package com.showcase.application.simplerestclient.models.dto.request;

import com.showcase.application.simplerestclient.models.dto.JsonBase;
import lombok.Builder;

@Builder
public record RestPagination(String sortProperty,
                             Integer offset,
                             Integer limit) implements JsonBase {
}

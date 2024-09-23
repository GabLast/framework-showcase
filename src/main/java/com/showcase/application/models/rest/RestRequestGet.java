package com.showcase.application.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class RestRequestGet extends RestBase {
    private Long id;
}

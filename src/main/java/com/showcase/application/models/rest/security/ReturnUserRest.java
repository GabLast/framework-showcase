package com.showcase.application.models.rest.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.RequestFrame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ReturnUserRest {
    private RequestFrame requestFrame;
    private UserRest userRest;
}

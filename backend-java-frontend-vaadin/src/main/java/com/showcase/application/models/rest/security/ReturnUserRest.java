package com.showcase.application.models.rest.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.showcase.application.models.rest.ResponseFrame;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class ReturnUserRest {
    private ResponseFrame responseFrame;
    private UserRest userRest;
    private String jwt;
}

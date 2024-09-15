package com.showcase.application.models.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestFrame {

    private Integer code = 0;
    private String message = "";
    private Boolean error = false;

}

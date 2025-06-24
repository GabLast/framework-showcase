package com.showcase.application.simplerestclient.models.dto;

import lombok.Data;

@Data
public class RequestFrame extends BaseClient {
    private Integer code = 0;
    private String message = "";
    private Boolean error = false;
}

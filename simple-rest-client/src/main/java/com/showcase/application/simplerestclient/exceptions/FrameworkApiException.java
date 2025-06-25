package com.showcase.application.simplerestclient.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FrameworkApiException extends RuntimeException {
    private Integer status;
    private String message;

    public FrameworkApiException(String message) {
        super(message);
        this.message = message;
    }
}

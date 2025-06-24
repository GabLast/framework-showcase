package com.showcase.application.simplerestclient.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InvalidRequestException extends RuntimeException {
    private Integer status;
    private String message;
}

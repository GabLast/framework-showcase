package com.showcase.application.utils.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.util.Locale;

@Data
@EqualsAndHashCode(callSuper = true)
public class MyException extends RuntimeException {

    public static final Integer CLIENT_ERROR = HttpStatus.BAD_REQUEST.value();
    public static final Integer NO_TOKEN_FOUND = HttpStatus.UNAUTHORIZED.value();
    public static final Integer SERVER_ERROR = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private Integer code;
    private String message;
    private Locale locale;

    public MyException() {
        super();
    }

    public MyException(String message) {
        super(message);
        this.message = message;
    }

    public MyException(Integer code, String message) {
        super(message);

        this.code = code;
        this.message = message;
    }
}

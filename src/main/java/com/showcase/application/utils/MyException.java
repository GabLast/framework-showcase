package com.showcase.application.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Locale;
import java.util.ResourceBundle;

@Data
@EqualsAndHashCode(callSuper = true)
public class MyException extends RuntimeException {

    public static final Integer CLIENT_ERROR = 400;
    public static final Integer SERVER_ERROR = 500;

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

    public MyException(Integer code, String key, Locale locale) {
        super(key);

        this.code = code;
        this.message = ResourceBundle.getBundle("message", locale).getString(key);;
    }
}

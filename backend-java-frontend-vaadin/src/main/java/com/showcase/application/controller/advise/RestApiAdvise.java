package com.showcase.application.controller.advise;

import com.showcase.application.models.rest.StandardResponse;
import com.showcase.application.utils.exceptions.MyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestApiAdvise {

    @ExceptionHandler(
            exception = MyException.class,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    protected StandardResponse badRequest(MyException ex, WebRequest request) {
        return StandardResponse.builder()
                .message(ex.getMessage())
                .status(ex.getCode())
                .path(request.getDescription(false))
                .build();
    }

//    @ExceptionHandler(
//            exception = UnAuthorizedException.class,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
//    protected StandardResponse badRequest(UnAuthorizedException ex, WebRequest request) {
//        return StandardResponse.builder()
//                .message(ex.getMessage())
//                .status(HttpStatus.UNAUTHORIZED.value())
//                .path(request.getDescription(false)) //maybe use httpservlet?
//                .build();
//    }
//
//    @ExceptionHandler(
//            exception = InternalServerErrorException.class,
//            produces = MediaType.APPLICATION_JSON_VALUE
//    )
//    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
//    protected StandardResponse badRequest(InternalServerErrorException ex, WebRequest request) {
//        return StandardResponse.builder()
//                .message(ex.getMessage())
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .path(request.getDescription(false))
//                .build();
//    }
}

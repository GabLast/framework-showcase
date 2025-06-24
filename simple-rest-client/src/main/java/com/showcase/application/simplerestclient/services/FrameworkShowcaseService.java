package com.showcase.application.simplerestclient.services;

import com.showcase.application.simplerestclient.exceptions.FrameworkApiException;
import com.showcase.application.simplerestclient.exceptions.InvalidCredentialsException;
import com.showcase.application.simplerestclient.exceptions.InvalidRequestException;
import com.showcase.application.simplerestclient.models.dto.Security.LoginResponse;
import com.showcase.application.simplerestclient.utils.BeanConstant;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FrameworkShowcaseService {

    private final WebClient.Builder frameworkShowcaseBuilder;

    public FrameworkShowcaseService(
            @Qualifier(BeanConstant.BEAN_FRAMEWORK_SHOWCASE_CLIENT_NAME) WebClient.Builder frameworkShowcaseBuilder
    ) {

        this.frameworkShowcaseBuilder = frameworkShowcaseBuilder;
    }


    public LoginResponse login() {
        WebClient.RequestHeadersUriSpec<?> request = frameworkShowcaseBuilder.build().get();

        WebClient.RequestHeadersSpec<?> params = request.uri(uriBuilder ->
                uriBuilder
                        .queryParam("param", "value")
                        .build()
        );


        return null;
    }

    private WebClient.ResponseSpec handleHttpStatusCodes(WebClient.ResponseSpec responseSpec) {
        responseSpec
                .onStatus(httpStatusCode ->
                                httpStatusCode.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse ->
                                Mono.just(new InvalidCredentialsException(HttpStatus.UNAUTHORIZED.value(), "Invalid Credentials")))
                .onStatus(httpStatusCode ->
                                httpStatusCode.equals(HttpStatus.FORBIDDEN),
                        clientResponse ->
                                Mono.just(new InvalidCredentialsException(HttpStatus.FORBIDDEN.value(), "Expired Token")))
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientresponse -> clientresponse
                                .bodyToMono(String.class)
                                .map(value -> new InvalidRequestException(HttpStatus.BAD_REQUEST.value(),
                                        "Bad Request from Client: " + value)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientresponse -> Mono.error(new FrameworkApiException(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Framework API Error: " + clientresponse.toString())))
        ;

        return responseSpec;
    }
}

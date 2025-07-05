package com.showcase.application.simplerestclient.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.simplerestclient.config.clients.FrameworkShowcaseClient;
import com.showcase.application.simplerestclient.exceptions.FrameworkApiException;
import com.showcase.application.simplerestclient.exceptions.InvalidCredentialsException;
import com.showcase.application.simplerestclient.exceptions.InvalidRequestException;
import com.showcase.application.simplerestclient.models.dto.request.BaseHeader;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequest;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestBody;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestGet;
import com.showcase.application.simplerestclient.models.dto.request.BaseRequestParams;
import com.showcase.application.simplerestclient.models.dto.request.authentication.RequestLogin;
import com.showcase.application.simplerestclient.models.dto.request.testdata.RequestTestData;
import com.showcase.application.simplerestclient.models.dto.request.testdata.RequestTestDataFindAll;
import com.showcase.application.simplerestclient.models.dto.response.RetryResponse;
import com.showcase.application.simplerestclient.models.dto.response.authentication.LoginResponse;
import com.showcase.application.simplerestclient.models.dto.response.testdata.TestDataFindAllResponse;
import com.showcase.application.simplerestclient.models.dto.response.testdata.TestDataResponse;
import com.showcase.application.simplerestclient.utils.BeanConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class FrameworkShowcaseService {

    private final WebClient.Builder frameworkShowcaseBuilder;
    private final ObjectMapper objectMapper;

    public FrameworkShowcaseService(
            @Qualifier(BeanConstant.BEAN_FRAMEWORK_SHOWCASE_CLIENT_NAME) WebClient.Builder frameworkShowcaseBuilder,
            ObjectMapper objectMapper
    ) {

        this.frameworkShowcaseBuilder = frameworkShowcaseBuilder;
        this.objectMapper = objectMapper;
    }

    public LoginResponse login(RequestLogin request) throws ExecutionException, InterruptedException {

        LoginResponse response = post(
                FrameworkShowcaseClient.ENDPOINT_LOGIN,
                request,
                LoginResponse.class
        )
                .toFuture().get();
//                .block();

        if (response == null) {
            throw new FrameworkApiException("The client did not receive a response from the Server");
        }

        return response;
    }

    public TestDataFindAllResponse findAllTestData(RequestTestDataFindAll request) throws ExecutionException, InterruptedException {

        TestDataFindAllResponse response = get(
                FrameworkShowcaseClient.ENDPOINT_TEST_DATA_API_FIND_ALL,
                request,
                TestDataFindAllResponse.class
        )
                .toFuture().get();
//                .block();

        if (response == null) {
            throw new FrameworkApiException("The client did not receive a response from the Server");
        }

        return response;
    }

    public TestDataResponse postTestData(RequestTestData request) throws ExecutionException, InterruptedException {

        TestDataResponse response = post(
                FrameworkShowcaseClient.ENDPOINT_TEST_DATA_API,
                request,
                TestDataResponse.class
        )
                .toFuture().get();
//                .block();

        if (response == null) {
            throw new FrameworkApiException("The client did not receive a response from the Server");
        }

        return response;
    }

    private <T, R> Mono<R> post(String endpoint,
                                BaseRequest<BaseRequestBody, BaseHeader, String> clientRequest,
                                Class<R> responseClass) {

        WebClient.RequestBodyUriSpec request = frameworkShowcaseBuilder
                .build()
                .post();

        WebClient.ResponseSpec responseSpec = request
                .uri(endpoint)
                .headers(httpHeaders -> {

                    if (!CollectionUtils.isEmpty(clientRequest.getHeaders().headers())) {
                        clientRequest.getHeaders().headers().forEach((key, value) -> {
                            httpHeaders.put(key, List.of(value));
                        });
                    }
                    httpHeaders.setBearerAuth(clientRequest.getBearer());
                })
                .bodyValue(clientRequest.getBody())
                .retrieve();

        handleHttpStatusCodes(responseSpec);

        return responseSpec.bodyToMono(String.class).mapNotNull(json -> {
            try {
//                log.info("API Response from {}: {}", endpoint, json);
                return objectMapper.readValue(json, responseClass);
            } catch (JsonProcessingException e) {
                log.info("Error parsing JSON {} to class {}", json, responseClass);
                return null;
            }
        });
    }

    private <T, R> Mono<R> get(String endpoint,
                               BaseRequestGet<BaseRequestParams, BaseHeader, String> clientRequest,
                               Class<R> responseClass) {

        WebClient.RequestHeadersUriSpec<?> request = frameworkShowcaseBuilder
                .build()
                .get();

        WebClient.RequestHeadersSpec<?> requestHeadersSpec = request
                .uri(endpoint,
                        uriBuilder -> {
                            if (!CollectionUtils.isEmpty(clientRequest.getParams().params())) {
                                clientRequest.getParams().params().forEach((key, value) -> {
                                    uriBuilder.queryParam(key, List.of(value));
                                });
                            }
                            return uriBuilder.build();
                        })
                .headers(httpHeaders -> {

                    if (!CollectionUtils.isEmpty(clientRequest.getHeaders().headers())) {
                        clientRequest.getHeaders().headers().forEach((key, value) -> {
                            httpHeaders.put(key, List.of(value));
                        });
                    }

                    httpHeaders.setBearerAuth(clientRequest.getBearer());
                });

        WebClient.ResponseSpec responseSpec = requestHeadersSpec.retrieve();

        handleHttpStatusCodes(responseSpec);

//        return responseSpec.bodyToMono(responseClass);

        return responseSpec.bodyToMono(String.class).mapNotNull(json -> {
            try {
//                log.info("API Response from {}: {}", endpoint, json);
                return objectMapper.readValue(json, responseClass);
            } catch (JsonProcessingException e) {
                log.info("Error parsing JSON {} to class {}", json, responseClass);
                return null;
            }
        });
    }

    private void handleHttpStatusCodes(WebClient.ResponseSpec responseSpec) {
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

    }

    public TestDataFindAllResponse findAllTestDataRetry(RequestTestDataFindAll request) throws ExecutionException, InterruptedException {

        return getWithRetry(
                request
        );
    }

    private TestDataFindAllResponse getWithRetry(BaseRequestGet<BaseRequestParams, BaseHeader, String> clientRequest) throws ExecutionException, InterruptedException {

        WebClient.RequestHeadersUriSpec<?> request = frameworkShowcaseBuilder
                .build()
                .get();

        WebClient.RequestHeadersSpec<?> requestHeadersSpec = request
                .uri(FrameworkShowcaseClient.ENDPOINT_TEST_DATA_API_FIND_ALL,
                        uriBuilder -> {
                            if (!CollectionUtils.isEmpty(clientRequest.getParams().params())) {
                                clientRequest.getParams().params().forEach((key, value) -> {
                                    uriBuilder.queryParam(key, List.of(value));
                                });
                            }
                            return uriBuilder.build();
                        })
                .headers(httpHeaders -> {

                    if (!CollectionUtils.isEmpty(clientRequest.getHeaders().headers())) {
                        clientRequest.getHeaders().headers().forEach((key, value) -> {
                            httpHeaders.put(key, List.of(value));
                        });
                    }

                    httpHeaders.setBearerAuth(clientRequest.getBearer());
                });

        WebClient.ResponseSpec responseSpec = requestHeadersSpec.retrieve();

        handleHttpStatusCodes(responseSpec);

        Mono<RetryResponse<TestDataFindAllResponse>> responseMono = responseSpec
                .bodyToMono(new ParameterizedTypeReference<
                        RetryResponse<TestDataFindAllResponse>>() {
                });

        @SuppressWarnings("unchecked")
        RetryResponse<TestDataFindAllResponse> response =
                (RetryResponse<TestDataFindAllResponse>) retryRequest(responseMono)
                        .toFuture().get();
//                        .block();

        if (response == null) {
            throw new FrameworkApiException("The client did not receive a response from the Server");
        }

        return response.response();
    }

    private Mono<?> retryRequest(Mono<?> responseMono) {
        return responseMono
                .retryWhen(Retry
                        .fixedDelay(3,
                                Duration.ofSeconds(15))
                        .filter(FrameworkApiException.class::isInstance));
    }
}

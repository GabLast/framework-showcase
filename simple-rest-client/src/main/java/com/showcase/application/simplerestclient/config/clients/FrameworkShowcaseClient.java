package com.showcase.application.simplerestclient.config.clients;

import com.showcase.application.simplerestclient.utils.BeanConstant;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class FrameworkShowcaseClient {

    //Base
//    public static final String BASE_URL = "localhost:8082";
    public static final String BASE_URL = "http://" + "127.0.0.1:8080";
    public static final String REST_ENDPOINT = "/rest";
    public static final String TEST_DATA_API = "/testdata";

    //Login
    public static final String ENDPOINT_LOGIN = "/auth/login";

    //Test Data API
    public static final String ENDPOINT_TEST_DATA_API = TEST_DATA_API; //base url for get/post
    public static final String ENDPOINT_TEST_DATA_API_FIND_ALL = ENDPOINT_TEST_DATA_API + "/findall";

    @Bean(name = BeanConstant.BEAN_FRAMEWORK_SHOWCASE_CLIENT_NAME)
    public WebClient.Builder frameworkShowcaseBuilder() {
        return WebClient.builder()
                .baseUrl(BASE_URL + REST_ENDPOINT)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .defaultHeader(HttpHeaders.ACCEPT_CHARSET, "UTF-8")
                .clientConnector(getClientConnector())
                .codecs(clientCodecConfigurer -> clientCodecConfigurer
                        .defaultCodecs()
                        .maxInMemorySize(10000000)); //byteCount -> between 5-10 mb?
    }

    private ReactorClientHttpConnector getClientConnector() {

        return new ReactorClientHttpConnector(HttpClient
                .create().compress(true)
                .resolver(nameResolverSpec -> nameResolverSpec
                        .cacheMaxTimeToLive(Duration.ofSeconds(30))
                        .build())
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler(
                                        30000,
                                        TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(
                                        30000,
                                        TimeUnit.MILLISECONDS))));
    }
}

package com.showcase.application.simplerestclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.simplerestclient.dto.request.BaseRequestParams;
import com.showcase.application.simplerestclient.dto.request.authentication.BodyLogin;
import com.showcase.application.simplerestclient.dto.request.authentication.RequestLogin;
import com.showcase.application.simplerestclient.dto.request.testdata.BodyTestData;
import com.showcase.application.simplerestclient.dto.request.testdata.RequestTestData;
import com.showcase.application.simplerestclient.dto.request.testdata.RequestTestDataFindAll;
import com.showcase.application.simplerestclient.dto.response.authentication.LoginResponse;
import com.showcase.application.simplerestclient.dto.response.testdata.TestDataFindAllResponse;
import com.showcase.application.simplerestclient.dto.response.testdata.TestDataResponse;
import com.showcase.application.simplerestclient.services.FrameworkShowcaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(100)
public class Runner implements ApplicationRunner {

    @Value("${spring.application.name}")
    private String appName;

    private final ObjectMapper objectMapper;
    private final FrameworkShowcaseService frameworkShowcaseService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            System.out.println("App Name: " + appName);

            //---------------------------postRequest Login

            RequestLogin credentials = RequestLogin.builder()
                    .body(BodyLogin.builder()
                            .username("admin")
                            .password("123")
                            .build())
                    .build();

            System.out.println("Login request: " + objectMapper.writeValueAsString(credentials));

            LoginResponse responseLogin = frameworkShowcaseService.login(credentials);
            System.out.println("Login response: " + objectMapper.writeValueAsString(responseLogin));

            //---------------------------getRequest Find All

            HashMap<String, String> params = new HashMap<>();
            params.put("word", "worddddddddddddddd2");
            params.put("description", "");
            params.put("dateStart", "");
            params.put("dateEnd", "");
            params.put("reportType", "");

            RequestTestDataFindAll requestTestDataFindAll = RequestTestDataFindAll.builder()
                    .baseRequestParams(BaseRequestParams
                            .builder()
                            .params(params)
                            .build())
                    .bearerToken(responseLogin.jwt())
                    .build();

            System.out.println("RequestTestDataFindAll request: " + objectMapper.writeValueAsString(requestTestDataFindAll));
            TestDataFindAllResponse testDataFindAllResponse = frameworkShowcaseService.findAllTestData(requestTestDataFindAll);
            System.out.println("TestDataFindAllResponse response: " + objectMapper.writeValueAsString(testDataFindAllResponse));

            //---------------------------getRequest with Retry

            System.out.println("Retry request: " + objectMapper.writeValueAsString(requestTestDataFindAll));
            TestDataFindAllResponse retry = frameworkShowcaseService.findAllTestDataRetry(requestTestDataFindAll);
            System.out.println("Retry response: " + objectMapper.writeValueAsString(testDataFindAllResponse));


            //---------------------------postRequest saveData

            RequestTestData requestTestData = RequestTestData.builder()
                    .bodyTestData(BodyTestData.builder()
                            .word("word from Rest Client")
                            .testTypeId(1L)
                            .date(new Date())
                            .number(BigDecimal.TWO)
                            .build())
                    .bearerToken(responseLogin.jwt())
                    .build();

            System.out.println("Post save request: " + objectMapper.writeValueAsString(requestTestData));
            TestDataResponse responseTestData = frameworkShowcaseService.postTestData(requestTestData);
            System.out.println("Post save response: " + objectMapper.writeValueAsString(responseTestData));


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

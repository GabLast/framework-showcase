package com.showcase.application.simplerestclient.controllers;

import com.showcase.application.simplerestclient.models.dto.request.authentication.BodyLogin;
import com.showcase.application.simplerestclient.models.dto.request.authentication.RequestLogin;
import com.showcase.application.simplerestclient.models.dto.response.authentication.LoginResponse;
import com.showcase.application.simplerestclient.services.FrameworkShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class SimpleController {

    private final FrameworkShowcaseService frameworkShowcaseService;

//    @GetMapping("/getLoginResponse")
//    public StandardResponse<LoginResponse> getLoginResponse() {
//        return new StandardResponse<>(frameworkShowcaseService.login(RequestLogin.builder()
//                .body(BodyLogin.builder()
//                        .username("admin")
//                        .password("123")
//                        .build())
//                .build()));
//    }

    @GetMapping("/getLoginResponse")
    public ResponseEntity<?> getLoginResponse() throws ExecutionException, InterruptedException {
        LoginResponse response = frameworkShowcaseService.login(RequestLogin.builder()
                .body(BodyLogin.builder()
                        .username("admin")
                        .password("123")
                        .build())
                .build());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

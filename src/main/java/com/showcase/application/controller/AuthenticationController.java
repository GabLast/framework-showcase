package com.showcase.application.controller;

import com.showcase.application.models.module.TestData;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.rest.module.ReturnTestData;
import com.showcase.application.models.rest.module.TestDataRest;
import com.showcase.application.models.rest.security.ReturnUserRest;
import com.showcase.application.models.rest.security.UserRest;
import com.showcase.application.models.security.User;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@CrossOrigin(maxAge = 1800, origins = "*")
@RequestMapping("rest/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final TranslationProvider translationProvider;
    private final AuthenticationService authenticationService;

    @GetMapping("/login")
    public ResponseEntity<?> getUser(@AuthenticationPrincipal User user) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnUserRest returnData = new ReturnUserRest();

        try {

            if(user == null) {
                throw new MyException(MyException.CLIENT_ERROR, "Invalid Credentials");
            }

            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(false);

            returnData.setRequestFrame(requestFrame);
            returnData.setUserRest(new UserRest(user));

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }
}

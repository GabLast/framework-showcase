package com.showcase.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.config.security.CustomAuthentication;
import com.showcase.application.models.rest.ResponseFrame;
import com.showcase.application.models.rest.dto.UserDto;
import com.showcase.application.models.rest.security.ReturnUserRest;
import com.showcase.application.models.rest.security.UserRest;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.utils.TranslationProvider;
import com.showcase.application.utils.exceptions.MyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@CrossOrigin(maxAge = 1800, origins = "*")
@RequestMapping("rest/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final TranslationProvider translationProvider;
    private final UserSettingService userSettingService;
    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "login",
            produces = "application/json")
    public ResponseEntity<?> login(@RequestBody UserDto userDto) {
        ResponseFrame responseFrame = new ResponseFrame();
        ReturnUserRest returnData = new ReturnUserRest();

        try {

            if (userDto == null) {
                throw new MyException(MyException.CLIENT_ERROR, "Invalid Credentials");
            }

            User user = authenticationService.login(userDto);

            responseFrame.setCode(HttpStatus.OK.value());
            returnData.setResponseFrame(responseFrame);

            Token token = authenticationService.findByUserAndEnabled(user, true);
            String jwt = authenticationService.generateJWT(token);

            if (token == null || StringUtils.isBlank(jwt)) {
                throw new MyException(MyException.CLIENT_ERROR, "This user doesnt have API Access");
            }

            returnData.setJwt(jwt);
            returnData.setUserRest(new UserRest(user));

            CustomAuthentication authentication = new CustomAuthentication(
                    token,
                    customUserDetailsService.getGrantedAuthorities(token.getUser()),
                    userSettingService.findByEnabledAndUser(true, user)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

//            System.out.println("Current Auth:");
//            System.out.println(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername());
//            System.out.println(((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getName());

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        } catch (MyException e) {
            responseFrame.setCode(HttpStatus.OK.value());
            responseFrame.setError(true);
            responseFrame.setMessage(e.getMessage());
            returnData.setResponseFrame(responseFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }
}

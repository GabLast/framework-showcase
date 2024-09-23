package com.showcase.application.controller;

import com.showcase.application.config.security.CustomAuthentication;
import com.showcase.application.models.rest.dao.UserDao;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.rest.security.ReturnUserRest;
import com.showcase.application.models.rest.security.UserRest;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.services.security.UserService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
//@CrossOrigin(maxAge = 1800, origins = "*")
@RequestMapping("rest/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final TranslationProvider translationProvider;
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDao userDao) {
        RequestFrame requestFrame = new RequestFrame();
        ReturnUserRest returnData = new ReturnUserRest();

        try {

            if (userDao == null) {
                throw new MyException(MyException.CLIENT_ERROR, "Invalid Credentials");
            }

            User user = authenticationService.login(userDao);

            requestFrame.setCode(HttpStatus.OK.value());
            returnData.setRequestFrame(requestFrame);

            Token token = authenticationService.findByUserAndEnabled(user, true);

            if(token == null) {
                throw new MyException(MyException.CLIENT_ERROR, "This user doesnt have API Access");
            }

            returnData.setJwt(authenticationService.generateJWT(token));
            returnData.setUserRest(new UserRest(user));

            CustomAuthentication authentication = new CustomAuthentication(token, customUserDetailsService.getGrantedAuthorities(token.getUser()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

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

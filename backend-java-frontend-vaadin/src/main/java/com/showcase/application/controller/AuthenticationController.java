package com.showcase.application.controller;

import com.showcase.application.config.security.CustomAuthentication;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.rest.dao.UserDao;
import com.showcase.application.models.rest.security.ReturnUserRest;
import com.showcase.application.models.rest.security.UserRest;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.services.security.UserService;
import com.showcase.application.utils.MyException;
import com.showcase.application.utils.TranslationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

//    @RequestMapping(value = "login", method = RequestMethod.POST)
    @GetMapping("login")
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
            String jwt = authenticationService.generateJWT(token);

            if(token == null || StringUtils.isBlank(jwt)) {
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
            requestFrame.setCode(HttpStatus.OK.value());
            requestFrame.setError(true);
            requestFrame.setMessage(e.getMessage());
            returnData.setRequestFrame(requestFrame);

            return new ResponseEntity<>(returnData, HttpStatus.OK);
        }
    }
}

package com.showcase.application.config.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.config.security.CustomAuthentication;
import com.showcase.application.models.rest.ResponseFrame;
import com.showcase.application.models.security.Token;
import com.showcase.application.models.security.User;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.utils.exceptions.MyException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserSettingService userSettingService;

    //    https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String authToken = request.getHeader("Authorization");
//            System.out.println("For Request: " + request.getServletPath() + "\n\n");
//            System.out.println("Token: " + authToken + "\n\n");
            if (authenticationService.isJWTValid(authToken) == null) {
                throw new MyException(HttpStatus.UNAUTHORIZED.value(), "Invalid Token");
            }

            String payload = authenticationService.getJWTPayload(authToken);
            if (StringUtils.isBlank(payload)) {
                throw new MyException(HttpStatus.UNAUTHORIZED.value(), "Corrupted Token");
            }

            Token token = authenticationService.findByTokenAndEnabled(payload, true);
            if (token == null) {
                throw new MyException(HttpStatus.UNAUTHORIZED.value(), "Corrupted Server Token");
            }

            CustomAuthentication authentication = new CustomAuthentication(
                    token,
                    customUserDetailsService.getGrantedAuthorities(token.getUser()),
                    userSettingService.findByEnabledAndUser(true, token.getUser())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == null) {
                throw new MyException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No user found");
            }

//            customUserDetailsService.grantAuthorities(token.getUser()); //could also do this
//            if (SecurityContextHolder.getContext().getAuthentication() != null) {
//                System.out.println("Login Principal");
//                System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
//            } else {
//                System.out.println("Context is null");
//            }

            filterChain.doFilter(request, response);

        } catch (MyException exception) {
            log.error(exception.getMessage());

            response.setStatus(exception.getCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(new ObjectMapper().writeValueAsString(new ResponseFrame(exception.getCode(), exception.getMessage(), true)));
            response.getWriter().flush();
            response.getWriter().close();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
//        https://stackoverflow.com/questions/52370411/springboot-bypass-onceperrequestfilter-filters
        boolean filter;

        filter = new AntPathMatcher().match("/rest/auth/login", request.getServletPath()) ||
                new AntPathMatcher().match("/vaadin/login", request.getServletPath()) ||
                new AntPathMatcher().match("/", request.getServletPath()) ||
                new AntPathMatcher().match("/vaadin", request.getServletPath()) ||
                new AntPathMatcher().match("/vaadin/**", request.getServletPath()) ||
                new AntPathMatcher().match("/VAADIN/**", request.getServletPath()) ||
                new AntPathMatcher().match("/images/**", request.getServletPath()) ||
                new AntPathMatcher().match("/offline-stub.html", request.getServletPath()) ||
                new AntPathMatcher().match("/manifest.webmanifest", request.getServletPath()) ||
                new AntPathMatcher().match("/sw.js", request.getServletPath()) ||
                new AntPathMatcher().match("/sw-runtime-resources-precache.js", request.getServletPath()) ||
                new AntPathMatcher().match("/icons/*.png", request.getServletPath()) ||
                new AntPathMatcher().match("/line-awesome/**", request.getServletPath())
        ;

//        System.out.println("For Request: " + request.getServletPath() + "\t | " + filter);

        return filter;
    }

}

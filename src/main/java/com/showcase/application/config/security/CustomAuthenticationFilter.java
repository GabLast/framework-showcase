//package com.showcase.application.config.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.showcase.application.models.rest.RequestFrame;
//import com.showcase.application.models.security.Token;
//import com.showcase.application.services.security.AuthenticationService;
//import com.showcase.application.services.security.CustomUserDetailsService;
//import com.showcase.application.utils.MyException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CustomAuthenticationFilter extends OncePerRequestFilter {
//
//    private final AuthenticationService authenticationService;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//
//        try {
//
//            String authToken = request.getHeader("Authorization");
//            System.out.println("Token: " + authToken + " For Request: " + request.getServletPath() + "\n\n");
//            if (!authenticationService.isJWTValid(authToken)) {
//                throw new MyException(MyException.CLIENT_ERROR, "Invalid Token");
//            }
//
//            String payload = authenticationService.getJWTPayload(authToken);
//            System.out.println("payload: " + payload);
//            if (StringUtils.isBlank(payload)) {
//                throw new MyException(MyException.CLIENT_ERROR, "Corrupted Token");
//            }
//
//            Token token = new Gson().fromJson(payload, Token.class);
//
//            CustomAuthentication authentication = new CustomAuthentication(token, customUserDetailsService.getGrantedAuthorities(token.getUser()));
//            SecurityContextHolder.getContext().setAuthentication(authentication);
////            customUserDetailsService.grantAuthorities(token.getUser()); //could also do this
//
//            if (SecurityContextHolder.getContext().getAuthentication() != null) {
//                System.out.println("Login Principal");
//                System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
//            } else {
//                System.out.println("Context is null");
//            }
//
//            filterChain.doFilter(request, response);
//
//        } catch (MyException exception) {
//            log.error(exception.getMessage());
//
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.getWriter().println(new ObjectMapper().writeValueAsString(new RequestFrame(exception.getCode(), exception.getMessage(), true)));
//            response.getWriter().flush();
//            response.getWriter().close();
//        }
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
////        https://stackoverflow.com/questions/52370411/springboot-bypass-onceperrequestfilter-filters
//        boolean filter;
//
//        filter = new AntPathMatcher().match("/rest/auth/login", request.getServletPath()) ||
//                new AntPathMatcher().match("/", request.getServletPath()) ||
//                new AntPathMatcher().match("/vaadin", request.getServletPath()) ||
//                new AntPathMatcher().match("/vaadin/***", request.getServletPath()) ||
//                new AntPathMatcher().match("/VAADIN/***", request.getServletPath()) ||
//                new AntPathMatcher().match("/images/**", request.getServletPath()) ||
//                new AntPathMatcher().match("/line-awesome/**", request.getServletPath())
//        ;
//        System.out.println(request.getServletPath() + " - Is Match: " + filter);
//
//        return filter;
//    }
//
//}

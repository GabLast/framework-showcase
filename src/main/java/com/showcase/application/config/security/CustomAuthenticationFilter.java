package com.showcase.application.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.showcase.application.models.rest.RequestFrame;
import com.showcase.application.models.security.Token;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.utils.MyException;
import com.vaadin.flow.server.VaadinSession;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String authToken = request.getHeader("X-Token");


//            System.out.println("Token: " + authToken);
//            if(true) {
//                Token token = authenticationService.findByTokenAndEnabled(authToken, true);
//                CustomAuthentication authentication = new CustomAuthentication(token, customUserDetailsService.getGrantedAuthorities(token.getUser()));
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//                customUserDetailsService.grantAuthorities(token.getUser()); //could also do this
//            }

            filterChain.doFilter(request, response);

        } catch (MyException exception) {
            log.error(exception.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(new ObjectMapper().writeValueAsString(new RequestFrame(exception.getCode(), exception.getMessage(), true)));
            response.getWriter().flush();
            response.getWriter().close();
        }
    }
}

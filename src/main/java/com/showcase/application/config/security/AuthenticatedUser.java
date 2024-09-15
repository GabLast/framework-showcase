package com.showcase.application.config.security;

import com.showcase.application.models.security.User;
import com.showcase.application.services.security.UserService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.servlet.ServletException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticatedUser {
    private final UserService userService;
    private final AuthenticationContext authenticationContext;

    public Optional<User> get() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .map(userDetails -> userService.findByUsername(userDetails.getUsername()));
    }

    public boolean isUserLoggedIn() {
        return get().isPresent();
    }

    public void logout() {
        try {
            VaadinServletRequest.getCurrent().getHttpServletRequest().logout();
//            authenticationContext.logout();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}

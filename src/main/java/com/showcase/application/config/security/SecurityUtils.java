package com.showcase.application.config.security;

import com.showcase.application.models.security.Permit;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SecurityUtils {

    public static boolean isAccessGranted(String permit) {
        // lookup needed role in user roles
        final List<String> allowedPrivilegios = Collections.singletonList("ROLE_" + permit);
        final Authentication userAuthentication = SecurityContextHolder.getContext().getAuthentication();

        //validando.
        return userAuthentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .anyMatch(allowedPrivilegios::contains);
    }

    public static void updateGrantedAuthorities(Set<Permit> permits) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Set<GrantedAuthority> updatedAuthorities = new HashSet<>();
        for (Permit privilegio : permits) {
            updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + privilegio.getName()));
        }

        Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(), updatedAuthorities);

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    //helper for automatic security field filler
    public static String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = "system";
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
        }

        return username;
    }
}

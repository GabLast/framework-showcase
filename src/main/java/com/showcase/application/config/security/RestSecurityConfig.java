//package com.showcase.application.config.security;
//
//import com.showcase.application.services.security.AuthenticationService;
//import com.showcase.application.services.security.CustomUserDetailsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.stereotype.Component;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//
//@Order(2)
//@RequiredArgsConstructor
//@EnableWebSecurity
//@Configuration
//@Component
//public class RestSecurityConfig {
//
//    private final AuthenticationService authenticationService;
//    private final CustomUserDetailsService customUserDetailsService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(authorizeRequests ->
//                                authorizeRequests
//                                        .requestMatchers("/rest/auth/login").permitAll()
////                                .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults())
//                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//////        https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
////                .addFilterBefore(new CustomAuthenticationFilter(authenticationService, customUserDetailsService), UsernamePasswordAuthenticationFilter.class)
//        ;
//
////        return super.filterChain(http);
//        return http.build();
//    }
//
//}

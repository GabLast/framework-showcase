package com.showcase.application.config.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    private final AppInfo appInfo;
    private final ParameterService parameterService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;

    //Vaadin requests
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
//                                .requestMatchers( new AntPathRequestMatcher("/rest/**")).anonymous() -> for rest ?
//                                .requestMatchers(new AntPathRequestMatcher("/dbconsole/**")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
//                                .anyRequest().authenticated()
                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults());

        //https://github.com/jcgueriaud1/remember-me
        http.rememberMe(
                httpSecurityRememberMeConfigurer ->
                        httpSecurityRememberMeConfigurer
                                .key(appInfo.getRememberMeToken())
                                .tokenValiditySeconds(86400)
                                .userDetailsService(customUserDetailsService)
        );

        super.configure(http);
        setLoginView(http, LoginView.class);
    }

    //Rest requests
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/rest/**").permitAll()
//                                .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////        https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
                .addFilterBefore(new CustomAuthenticationFilter(authenticationService, customUserDetailsService), UsernamePasswordAuthenticationFilter.class);

        return super.filterChain(http);
    }

    @Override
    protected void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(
                // (development mode) H2 debugging console
                "/dbconsole/*"
        );
    }
}

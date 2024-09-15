package com.showcase.application.config.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    private final AppInfo appInfo;
    private final ParameterService parameterService;
    private final CustomUserDetailsService customUserDetailsService;

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
//                .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

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

    @Override
    protected void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(
                // (development mode) H2 debugging console
                "/dbconsole/*"
        );
    }
}

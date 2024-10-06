package com.showcase.application.config.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.services.configuration.ParameterService;
import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import com.showcase.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends VaadinWebSecurity {

    private final AppInfo appInfo;
    private final ParameterService parameterService;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;
    private final UserSettingService userSettingService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(
                        requests -> requests
                                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**/*.svg")).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/images/*")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/rest/auth/login", HttpPost.METHOD_NAME)).permitAll()
                                .requestMatchers(new AntPathRequestMatcher("/rest/**")).permitAll()
//                                .requestMatchers(new AntPathRequestMatcher("/dbconsole/**")).permitAll()
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->
                                authorizeRequests
                                        .requestMatchers(HttpMethod.GET, "/rest/auth/login").permitAll() // -> http method POST doesnt work for some reason
                                        .requestMatchers("/rest/**").authenticated()
//                                        .requestMatchers("/rest/**").permitAll()
                )
                .httpBasic(withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////        https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/
                .addFilterBefore(new CustomAuthenticationFilter(authenticationService, customUserDetailsService, userSettingService), UsernamePasswordAuthenticationFilter.class)
        ;

        return super.filterChain(http);
//        return http.build();
    }

    //    https://www.baeldung.com/spring-cors
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


//    @Override
//    protected void configure(WebSecurity web) throws Exception {
//        web.ignoring().requestMatchers(
//                "/rest/auth/login"
//        );
//    }

}

package com.showcase.application.config.security;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.views.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
@Order()
public class VaadinSecurityConfig extends VaadinWebSecurity {

    private final AppInfo appInfo;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((
                                authorize) ->
                                authorize
                                        .requestMatchers("line-awesome/**").permitAll()
                                        .requestMatchers("/images/*").permitAll()
                )
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
//                .sessionManagement(httpSecuritySessionManagementConfigurer -> // vaadin doesn't allow for it being stateless.
//                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(new CustomAuthenticationFilter(authenticationService, customUserDetailsService, userSettingService), UsernamePasswordAuthenticationFilter.class)
        ;
////        https://www.geeksforgeeks.org/spring-boot-3-0-jwt-authentication-with-spring-security-using-mysql-database/

        //https://github.com/jcgueriaud1/remember-me
//        http.rememberMe(
//                httpSecurityRememberMeConfigurer ->
//                        httpSecurityRememberMeConfigurer
//                                .key(appInfo.getRememberMeToken())
//                                .tokenValiditySeconds(86400)
//                                .userDetailsService(customUserDetailsService)
//        );

        super.configure(http);
        setLoginView(http, LoginView.class);
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        // Customize your WebSecurity configuration.
        // ignore the api because vaadin doesn't
        // manage the requests properly
        web.ignoring().requestMatchers("/rest/**");
        super.configure(web);
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
}

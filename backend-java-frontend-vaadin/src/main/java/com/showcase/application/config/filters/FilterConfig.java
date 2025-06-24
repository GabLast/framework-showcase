package com.showcase.application.config.filters;

import com.showcase.application.services.configuration.UserSettingService;
import com.showcase.application.services.security.AuthenticationService;
import com.showcase.application.services.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    //Fix to Vaadin blocking post requests.
    //
    //https://stackoverflow.com/questions/79364651/update-spring-boot-3-4-1-spring-security-error-a-filter-chain-that-matches-any
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationService authenticationService;
    private final UserSettingService userSettingService;

    @Bean
    public FilterRegistrationBean<CustomAuthenticationFilter> afterAuthFilterRegistrationBean(
            SecurityProperties securityProperties) {

        var filterRegistrationBean = new FilterRegistrationBean<CustomAuthenticationFilter>();

        // a filter that extends OncePerRequestFilter
        filterRegistrationBean.setFilter(new CustomAuthenticationFilter(authenticationService, customUserDetailsService, userSettingService));

        // this needs to be a number greater than than spring.security.filter.order
        filterRegistrationBean.setOrder(securityProperties.getFilter().getOrder() + 1);
        return filterRegistrationBean;
    }
}

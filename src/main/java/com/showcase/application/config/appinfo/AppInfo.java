package com.showcase.application.config.appinfo;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppInfo {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    //    @Value("${spring.profiles.active}") -> has to start the app a spring profile or
    private String appProfile;

}

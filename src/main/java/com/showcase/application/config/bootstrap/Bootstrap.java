package com.showcase.application.config.bootstrap;

import com.showcase.application.services.other.TestTypeService;
import com.showcase.application.services.security.UserService;
import com.showcase.application.utils.TranslationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
@RequiredArgsConstructor
@Slf4j
public class Bootstrap implements ApplicationRunner {

    private final TestTypeService testTypeService;
    private final UserService userService;
    private final TranslationProvider translationProvider;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            log.info("Creating Pre-defined values");
            testTypeService.bootstrap();

            log.info("Creating admin user");
            userService.bootstrap();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

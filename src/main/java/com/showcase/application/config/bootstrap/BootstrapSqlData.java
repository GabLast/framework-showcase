package com.showcase.application.config.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
@RequiredArgsConstructor
@Slf4j
//@SQLInsert(sql = "/data/test_type.sql")
public class BootstrapSqlData implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

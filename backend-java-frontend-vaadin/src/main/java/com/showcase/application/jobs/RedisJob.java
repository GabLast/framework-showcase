package com.showcase.application.jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisJob {
    //https://www.freeformatter.com/cron-expression-generator-quartz.html -> good cron website

    @Scheduled(fixedDelay = 600000)
    public void testDataRedis() {
        try {


        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}

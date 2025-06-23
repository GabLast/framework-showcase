package com.showcase.application.config.bootstrap;

import com.showcase.application.services.redis.RedisTestRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 3)
@RequiredArgsConstructor
@Slf4j
public class BootstrapRedis implements ApplicationRunner {

    private final RedisTestRedisService redisTestRedisService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            redisTestRedisService.bootstrap();
            log.info("Redis bootstrap done");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

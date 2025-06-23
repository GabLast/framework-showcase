package com.showcase.application.services.redis;

import com.showcase.application.config.redis.RedisConfig;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.utils.exceptions.MyException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class RedisTestRedisService {

    private final TestDataService testDataService;
    private final ValueOperations<String, Integer> simpleDataCountCache;

    //Keys
    public static final String SIMPLE_DATA_COUNT = "simpleDataCount";

    public RedisTestRedisService(TestDataService testDataService,
                                 @Qualifier("simpleDataCount") RedisTemplate<String, Integer> simpleDataCount) {
        this.testDataService = testDataService;
        this.simpleDataCountCache = simpleDataCount.opsForValue();
    }

    public void bootstrap() {
        try {
            simpleDataCountCache.set(
                    SIMPLE_DATA_COUNT,
                    testDataService.findAll().size(),
                    Duration.ofMinutes(RedisConfig.TIME_TO_LIVE));
        } catch (MyException e) {
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public Optional<Integer> getSimpleDataCountCache() {
        try {
            Integer value = simpleDataCountCache.get(SIMPLE_DATA_COUNT);
            if (value != null) {
                return Optional.of(value);
            } else {
                return Optional.empty();
            }
        } catch (MyException e) {
            if (e instanceof MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

}

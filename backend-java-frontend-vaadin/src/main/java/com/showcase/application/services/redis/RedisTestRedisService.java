package com.showcase.application.services.redis;

import com.showcase.application.models.redis.RedisTest;
import com.showcase.application.repositories.redis.RedisTestRedisRepository;
import com.showcase.application.services.module.TestDataService;
import com.showcase.application.utils.MyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisTestRedisService {

    private final TestDataService testDataService;
    private final RedisTestRedisRepository redisTestRedisRepository;

    public void bootstrap() {
        try {
            RedisTest redisTest = new RedisTest();
            redisTest.setId("testdata");
            redisTest.setTestTypeCount(testDataService.findAll().size());
            redisTestRedisRepository.save(redisTest);

        } catch (MyException e) {
            if(e instanceof  MyException) {
                throw e;
            } else {
                throw new MyException(MyException.SERVER_ERROR, e.getMessage());
            }
        }
    }

    public Optional<RedisTest> findById(String id) {
        return redisTestRedisRepository.findById(id);
    }
}

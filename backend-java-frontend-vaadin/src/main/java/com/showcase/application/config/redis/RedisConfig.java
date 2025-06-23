package com.showcase.application.config.redis;

import com.showcase.application.config.appinfo.AppInfo;
import com.showcase.application.services.redis.RedisTestRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class RedisConfig {

    private final AppInfo appInfo;
    private final String HOST = "localhost";
    private final String PASSWORD = "localhost";
    private final int PORT = 26379;
    public static final int TIME_TO_LIVE = 1440; //#1 day;

    @Bean
    protected LettuceConnectionFactory getConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST, PORT);
        char[] redisPassword = PASSWORD.toCharArray();

        // Set the password only if it's present (local may not have a password)
        if (redisPassword != null && redisPassword.length > 0) {
            redisStandaloneConfiguration.setPassword(PASSWORD);
        }

        LettuceConnectionFactory redisConnectionFactory =
                new LettuceConnectionFactory(redisStandaloneConfiguration);
        redisConnectionFactory.start();

        return redisConnectionFactory;
    }

    private RedisCacheConfiguration getDefaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(TIME_TO_LIVE))
                .disableCachingNullValues();
    }

    @Bean
    protected RedisCacheManager redisStarter() {
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(getConnectionFactory())
                .withCacheConfiguration(
                        RedisTestRedisService.SIMPLE_DATA_COUNT,
                        getDefaultCacheConfig())
                .build();
    }

    @Bean("simpleDataCount")
    protected RedisTemplate<String, Integer> simpleDataCount() {
        RedisTemplate<String, Integer> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(getConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
}

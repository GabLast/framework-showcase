package com.showcase.application.repositories.redis;

import com.showcase.application.models.redis.RedisTest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedisTestRedisRepository extends CrudRepository<RedisTest, String> {
    List<RedisTest> findAlL();

    Optional<RedisTest> findById(String id);
}
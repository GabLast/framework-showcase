package com.showcase.application.models.redis;

import com.showcase.application.models.RedisBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RedisTest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisTest extends RedisBase {
    Integer testTypeCount;
}

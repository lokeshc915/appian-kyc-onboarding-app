
package com.example.kyc.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

  @Bean(destroyMethod = "shutdown")
  public RedisClient redisClient(@Value("${app.redis.uri:redis://localhost:6379}") String uri) {
    return RedisClient.create(uri);
  }

  @Bean(destroyMethod = "close")
  public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient client) {
    return client.connect(new io.lettuce.core.codec.StringCodec(), new io.lettuce.core.codec.ByteArrayCodec());
  }
}

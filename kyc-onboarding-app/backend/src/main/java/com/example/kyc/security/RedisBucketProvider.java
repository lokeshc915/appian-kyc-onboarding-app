
package com.example.kyc.security;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.stereotype.Component;

@Component
public class RedisBucketProvider {

  private final ProxyManager<String> proxyManager;

  public RedisBucketProvider(StatefulRedisConnection<String, byte[]> connection) {
    this.proxyManager = LettuceBasedProxyManager.builderFor(connection).build();
  }

  public Bucket resolveBucket(String key, RateLimitPolicy policy) {
    BucketConfiguration config = BucketConfiguration.builder()
        .addLimit(Bandwidth.classic(policy.capacity(), Refill.greedy(policy.capacity(), policy.refillPeriod())))
        .build();
    return proxyManager.builder().build(key, () -> config);
  }
}

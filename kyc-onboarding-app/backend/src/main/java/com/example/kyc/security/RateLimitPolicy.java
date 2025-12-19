
package com.example.kyc.security;

import java.time.Duration;

public record RateLimitPolicy(long capacity, Duration refillPeriod) {}

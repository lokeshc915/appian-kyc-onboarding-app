package com.example.kyc.sla;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.sla")
public record SlaProperties(
    long step1Hours,
    long step2Hours,
    long reviewHours,
    long checkIntervalSeconds
) {
}

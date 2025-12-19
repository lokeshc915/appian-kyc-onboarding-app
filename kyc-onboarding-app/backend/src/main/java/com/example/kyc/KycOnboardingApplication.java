package com.example.kyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.kyc.sla.SlaProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SlaProperties.class)
public class KycOnboardingApplication {
  public static void main(String[] args) {
    SpringApplication.run(KycOnboardingApplication.class, args);
  }
}

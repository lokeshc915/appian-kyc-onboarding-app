package com.example.kyc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI kycOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("KYC Onboarding API")
            .version("1.0.0")
            .description("Appian-style onboarding workflows implemented in Spring Boot services (no BPM engine)."));
  }
}

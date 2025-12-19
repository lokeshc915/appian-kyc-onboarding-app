
package com.example.kyc.config;

import com.example.kyc.security.AbacConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AbacConfig.class})
public class SecurityPropertiesConfig {}

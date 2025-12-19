
package com.example.kyc.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app.abac")
public class AbacConfig {
  private List<String> allowedCountries = List.of();
  private String maxRiskLevel = "MEDIUM";

  public List<String> getAllowedCountries() { return allowedCountries; }
  public void setAllowedCountries(List<String> allowedCountries) { this.allowedCountries = allowedCountries; }

  public String getMaxRiskLevel() { return maxRiskLevel; }
  public void setMaxRiskLevel(String maxRiskLevel) { this.maxRiskLevel = maxRiskLevel; }
}

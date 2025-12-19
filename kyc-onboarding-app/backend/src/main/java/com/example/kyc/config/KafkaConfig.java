
package com.example.kyc.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

  @Value("${app.kafka.topics.case-events}")
  private String caseTopic;

  @Bean
  public NewTopic caseEventsTopic() {
    // 6 partitions for parallelism, replication=1 (local/dev)
    return new NewTopic(caseTopic, 6, (short) 1);
  }
}

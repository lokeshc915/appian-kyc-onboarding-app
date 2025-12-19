
package com.example.kyc.stream;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CaseEventConsumer {

  @KafkaListener(
      topics = "${app.kafka.topics.case-events}",
      groupId = "kyc-audit-consumer")
  public void consume(CaseEvent event) {
    // Example consumer: write to logs / analytics / downstream systems
    System.out.println("Consumed case event: " + event);
  }
}

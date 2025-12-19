
package com.example.kyc.stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CaseEventProducer {

  private final KafkaTemplate<String, CaseEvent> kafka;
  private final String topic;

  public CaseEventProducer(
      KafkaTemplate<String, CaseEvent> kafka,
      @Value("${app.kafka.topics.case-events}") String topic) {
    this.kafka = kafka;
    this.topic = topic;
  }

  public void publish(CaseEvent event) {
    // key = country ensures partitioning by geography
    kafka.send(topic, event.country(), event);
  }
}

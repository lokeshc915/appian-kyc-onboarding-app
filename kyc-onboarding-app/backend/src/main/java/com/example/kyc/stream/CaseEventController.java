
package com.example.kyc.stream;

import org.springframework.web.bind.annotation.*;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/streams")
public class CaseEventController {

  private final CaseEventProducer producer;

  public CaseEventController(CaseEventProducer producer) {
    this.producer = producer;
  }

  @PostMapping("/case-event")
  public void publish(@RequestBody CaseEvent event) {
    producer.publish(
        new CaseEvent(
            event.caseId(),
            event.eventType(),
            event.status(),
            event.country(),
            event.riskLevel(),
            OffsetDateTime.now()
        )
    );
  }
}

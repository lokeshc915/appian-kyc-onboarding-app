
package com.example.kyc.stream;

import java.time.OffsetDateTime;

public record CaseEvent(
    Long caseId,
    String eventType,
    String status,
    String country,
    String riskLevel,
    OffsetDateTime occurredAt
) {}

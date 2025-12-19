
# Kafka Advanced Architecture & Use Cases (KYC)

## 1. Schema Registry (Avro / JSON Schema)
- Ensures backward/forward compatibility
- Prevents breaking producers/consumers
- Recommended: Confluent Schema Registry

Schemas:
- CaseEvent.avsc
- RiskScoreEvent.avsc

Compatibility:
- BACKWARD for prod
- FULL for regulated audit topics

## 2. Exactly-Once Processing (EOS)
Enabled via:
- `processing.guarantee=exactly_once_v2`
- Idempotent producers
- Transactional Kafka Streams

Use cases:
- Audit trail
- Risk score computation
- Regulatory reporting

## 3. Kafka Streams – Real-time Risk Scoring
Topology:
- Input: kyc.case.events
- Enrich with country + risk rules
- Output: kyc.case.risk.scored

Sample logic:
- HIGH risk → emit alert
- MEDIUM → manual review
- LOW → auto-progress

## 4. Dead Letter Queue (DLQ)
Topics:
- kyc.case.events.dlq

Triggers:
- Deserialization errors
- Schema mismatch
- Business validation failures

## 5. Event Replay (Audit / Recovery)
- Reset consumer offsets
- Reprocess historical events
- Used for audits and backfills

Commands:
- kafka-consumer-groups --reset-offsets

## 6. Environment Strategy

| Env | Partitions | Replication | Registry |
|----|-----------|-------------|----------|
| Local | 3 | 1 | Local |
| Test | 6 | 2 | Shared |
| Prod | 12+ | 3 | HA |

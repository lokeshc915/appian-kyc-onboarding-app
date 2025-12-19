
# Kafka Stream Design – KYC Onboarding

## Topic
`kyc.case.events`

## Partitions
- 6 partitions
- Key = `country`
- Guarantees ordering per country
- Enables parallel consumers per geography

## Event Types
- CASE_CREATED
- CASE_SUBMITTED
- DOCUMENT_UPLOADED
- CASE_APPROVED
- CASE_REJECTED
- SLA_BREACHED

## Producers
- CaseEventProducer
- Triggered from service layer on status changes

## Consumers
- Audit consumer
- Analytics / Risk engine
- Notification service

## Environments
- Local: docker-compose Kafka
- Test: shared Kafka cluster
- Prod: MSK / Confluent Cloud


# Advanced Security

## Per-endpoint rate limits (Redis-backed Bucket4j)
- login/register: 10/min
- upload docs: 30/min
- admin: 60/min
- default: 100/min
Stored in Redis for cluster-safe behavior.

## ABAC for KYC document access
Attributes:
- country
- riskLevel (LOW/MEDIUM/HIGH)

Config:
- APP_ABAC_ALLOWED_COUNTRIES
- APP_ABAC_MAX_RISK_LEVEL

## Signed URL downloads (S3/Azure)
Generate time-boxed links for cloud storage:
- /api/documents/{docId}/signed-url?ttlSeconds=300

## WAF-style upload rules + virus scan hook
- size limit
- content-type allowlist
- VirusScanService hook (NoOp default)

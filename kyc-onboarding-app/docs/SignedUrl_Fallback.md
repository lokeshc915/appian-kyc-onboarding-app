
# Signed URL Fallback (Cloud vs Local)

## Objective
UI must not know where files are stored.

## Endpoint
GET /api/documents/{docId}/download-link

## Response
Cloud (S3/Azure):
{
  "mode": "SIGNED_URL",
  "url": "https://signed-url..."
}

Local filesystem:
{
  "mode": "STREAM",
  "url": "/api/documents/{docId}/download"
}

## Profiles
- local, docker -> STREAM
- s3, azure -> SIGNED_URL

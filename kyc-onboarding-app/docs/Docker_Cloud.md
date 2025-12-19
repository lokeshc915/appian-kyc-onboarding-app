# Docker + Cloud Profiles

## Docker (local demo)
1. From repo root:
   - `docker compose up --build`
2. Open:
   - Frontend: `http://localhost:4200`
   - Backend: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

## Cloud-ready storage
Storage is profile-based via `BlobStorage`:

- `local` / `docker` (default): **Local filesystem**
  - `app.storage.base-dir=/data/files`
- `s3`: **AWS S3**
  - `app.storage.s3.bucket=...`
  - `app.storage.s3.prefix=...`
  - plus AWS credentials via env/instance profile
- `azure`: **Azure Blob**
  - `app.storage.azure.connection-string=...`
  - `app.storage.azure.container=...`
  - `app.storage.azure.prefix=...`

Run backend with profile:
- `SPRING_PROFILES_ACTIVE=s3`
- `SPRING_PROFILES_ACTIVE=azure`


# Quality Guide (Tests, Coverage, SonarQube)

## Backend (JUnit + JaCoCo)
Run:
- `mvn -f backend/pom.xml test`

Reports:
- HTML: `backend/target/site/jacoco/index.html`
- XML: `backend/target/site/jacoco/jacoco.xml`

## Frontend (Angular)
Run:
- `npm test`
Coverage:
- `npm run test:coverage`
Report:
- `frontend/coverage/lcov.info`

## SonarQube (local)
1. Start:
- `docker compose up sonarqube`
2. Open:
- http://localhost:9000
3. Run scanner:
- `sonar-scanner -Dsonar.host.url=http://localhost:9000 -Dsonar.login=<token>`

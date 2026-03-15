# Production Readiness Checklist & Findings - Andromeda Authorization Server

This document summarizes the production readiness analysis of the project.

## ✅ Completed Improvements

### 1. Security Configuration
- **CORS Configuration**: Restricted CORS to trusted domains and their subdomains: `*.milkyway`, `*.test.milkyway`, `*.dev.milkyway`. This improves security by preventing unauthorized external sites from accessing the API.
- **Actuator Security**: Secured Actuator endpoints. General endpoints (`/api/actuator/**`) now require `ROLE_ADMIN`, while `/api/actuator/health` and `/api/actuator/info` remain public but with restricted details.
- **CSRF**: Remains disabled, which is standard for stateless JWT-based APIs.

### 2. Error Handling & Logging
- **Structured Error Responses**: Replaced plain-text error messages with a structured JSON `ErrorResponse` containing status code, message, and timestamp.
- **Global Exception Handling**: Added a general `Exception` handler to catch unexpected errors and log them properly.
- **Logging**: Integrated SLF4J logging in `GlobalExceptionHandler` to record warnings for access denials/authentication failures and errors for runtime exceptions.

### 3. Monitoring (Spring Boot Actuator)
- **Hardened Settings**: Changed `management.endpoint.prometheus.access` from `unrestricted` to `restricted`.
- **Health Details**: Changed `management.endpoint.health.show-details` from `always` to `when_authorized`.

## 💡 Recommendations for Deployment

### 1. Environment Variables
Ensure the following variables are securely managed (e.g., via Docker secrets, Kubernetes secrets, or a secure vault):
- `APP_JWT_SECRET`: Use a strong, randomly generated Base64-encoded string (at least 64 bytes).
- `ANDROMEDA_DB_PASSWORD`: Use a strong password.
- `AUTH_MAIL_PASSWORD`: Use an App Password if using Google SMTP.

### 2. Database
- The current HikariCP settings (pool size 20) are reasonable for a start. Monitor pool usage in production via Actuator metrics and adjust if necessary.
- Ensure the MariaDB instance is hardened and not exposed directly to the public internet.

### 3. SSL/TLS
- The application currently relies on an external reverse proxy (like Traefik, as mentioned in `HELP.md`) for SSL/TLS termination. Ensure that the connection between the proxy and the application is also secure if they are not on the same trusted network.

### 4. CORS Policy
- In `SecurityConfig.java`, update `corsConfigurationSource` to allow only trusted origins instead of `*` before the final release to a public environment.

### 5. Dependency Vulnerabilities
- Regularly run `mvn dependency-check:check` (if the plugin is added) or use GitHub's Dependabot to stay informed about vulnerabilities in libraries like `jjwt` or Spring Boot.

## 📊 Summary of Readiness
The application is now better prepared for production with secured monitoring, structured error reporting, and explicit security policies.

# Andromeda Auth Api Changelog


## v 5.1.0-PUBLIC

- **Static Code Analysis**:
    - Added `qodana.yaml` to the repository for consistent code quality checks across the team and CI/CD.
    - Configured Qodana for JVM projects.
- **API Documentation & Testing**:
    - Fully reorganized and updated all `.http` test files in `src/test/endpoints/`.
    - Moved database-related endpoints to a new `table/` directory structure to better reflect the API architecture.
    - Added missing documentation for `auth/logout`, `auth/logout-all`, and `RefreshTokenIncidentController`.
    - Translated all endpoint descriptions and comments to English for international consistency.
    - Standardized request headers and URI formats across all `.http` files.
- **Project Maintenance**:
    - Bumped project version to `5.1.0-PUBLIC` in `pom.xml`.
    - Updated **Spring Boot** to version `3.5.11` for the latest security patches and features.
    - Integrated **JJWT** (Java JWT) version `0.13.0` with separate dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) for modern JWT handling.


## v 5.0.0

- **Security & JWT Overhaul**:
    - Implemented **Refresh Token Rotation**: old refresh tokens are marked as revoked upon use.
    - Introduced **Refresh Token Incident Tracking**: security incidents (replay attacks, invalid tokens) are now logged in a dedicated table `refresh_token_incidents` for audit.
    - Added **Token Versioning**: a `token_version` is now stored in the user table and verified against JWT claims. Password changes and manual logouts increment this version, effectively invalidating all existing tokens.
    - Enhanced **Token Encryption**: refresh tokens and confirmation tokens are now encrypted before being stored in the database using AES-256 (`EncryptionUtil`).
    - Implemented **jti (JWT ID)**: unique identifiers for all tokens to prevent replay attacks and allow granular revocation.
    - Reduced **Token TTL**: aligned with security best practices (Access: 5 min, Refresh: 1 hour).
    - Hardened **Cookie Security**: forced `HttpOnly`, `Secure`, and `SameSite=Lax` flags for all authentication cookies.
    - Improved **CSRF Protection**: enabled Spring Security CSRF with `CookieCsrfTokenRepository`.
- **Monitoring & Observability**:
    - Integrated **Micrometer/Prometheus Metrics**: added custom timers and counters for authentication success, failures, token refreshes, and logouts.
    - Enhanced **Security Logging**: structured logging for authentication events, including failed login attempts and suspicious refresh token activity.
- **API & Architecture Refactoring**:
    - Simplified **Role-Based Access Control**: restricted administrative and token management endpoints strictly to `ROLE_ADMIN` or `ROLE_MODERATOR`.
    - Refactored **Model Assemblers**: replaced `BeanUtils` with manual property mapping for better performance and type safety (moved from `String` to `int` for versions).
    - Improved **Account Processes**: refactored `AccountProcessFactory` to use Spring's `ObjectProvider` instead of `ApplicationContext`.
    - Standardized **Request DTOs**: moved all request implementations to a dedicated `impl` package and improved validation annotations.
    - Updated **API Contracts**: changed `Reset Password` endpoint to use query parameters instead of path variables.
- **Comprehensive Testing**:
    - **Rewrote and expanded unit tests**: achieved high coverage (98.6% classes, 92.1% lines) across all modules (controllers, services, repositories, builders, security).
    - Added specific tests for: `GlobalExceptionHandler`, all `Mapper` classes, `Security Filters`, `Encryption/JWT Utils`, and `Domain Builders`.
- **Bugfixes**:
    - Fixed `NullPointerException` in `RoleApiMapper` and `UserSecurity`.
    - Corrected HATEOAS link generation in various model assemblers.
    - Fixed race conditions in repositories by removing manual ID increments.

## v 4.1.0-PUBLIC

- introduced `UserRequest` to unify the API contract in `UserController`.
- refactored `UserController` to use `UserRequest` instead of the domain `User` model for creating and updating users.
- updated `UserApiMapper` with mapping logic for `UserRequest`.
- improved architectural consistency by isolating the domain model from the API layer.
- **Production Readiness Updates**:
    - configured restricted CORS policy in `SecurityConfig` (restricted to `*.milkyway`, `*.test.milkyway`, `*.dev.milkyway`).
    - secured Actuator endpoints (restricted to `ROLE_ADMIN`).
    - implemented structured JSON error responses with `ErrorResponse`.
    - enhanced `GlobalExceptionHandler` with better logging and general exception handling.
    - hardened Actuator settings in `application.properties`.
    - created `docs/PROD_READY_CHECKLIST.md` with detailed findings and recommendations.
- **Bugfixes**:
    - added `MethodArgumentNotValidException` handling in `GlobalExceptionHandler` to return `400 Bad Request` for validation errors.
    - added `MissingServletRequestParameterException` handling in `GlobalExceptionHandler` to return `400 Bad Request` for missing required query parameters.
    - fixed `AuthControllerTest` and `AccountControllerTest` to align with security filters and proper error handling.

## v 4.0.0

- updated a spring boot pattern to version 3.2.12 for resolving security vulnerabilities
- removed the spring-cloud dependency, currently settings will be loaded via docker env vars
- created `HealthController` with a simple hello endpoint and version endpoint
- updated application.properties and README accordingly
- set a default port to 8080
- added a CI / CD pipeline with GitHub actions for building and testing the application on a test environment
- added a CI / CD pipeline with GitHub actions for building the application on prod environment
- added testing and coverage documentation. See [TESTING.md](TESTING.md) for more details.
- support for Prometheus metrics - api /api/actuator. See [METRICS.md](METRICS.md) for more details.
```alert
  management.endpoint.prometheus.access=unrestricted - need to be changed in production environment to more secure
  settings
```
- generated endpoint documentation. See [ENDPOINTS.md](ENDPOINTS.md) for more details (including IntelliJ HTTP client examples).
- updated [HELP.md](HELP.md) to reflect that certificates are now managed via Traefik and archived old self-signed certificate steps.
- removed the JENKINS.md file, replaced with CI / CD pipeline in GitHub actions.

## v 3.1.3

- Adjust `application.properties` and `application-test.properties` for better test and production support.

```properties
  milkyway.url=${MILKYWAY_URL}
spring.cloud.config.uri=${milkyway.url}
spring.config.import=optional:configserver:${milkyway.url}/andromeda-cloud-server/cloud-authorization
server.port=8444
```

- Update default port to 8444 to avoid conflicts with Docker environment.
- Enhance README with refined setup instructions and formatting updates.

## v 3.1.1

- Bugfix and update with a refresh token and authorization response.

## v 3.1.0 Release

- Added endpoint for handling access token refresh/generation based on refreshToken cookie.
- Modified and fixed issues with incorrect cookie expiration dates.
- Updated unit tests

## v 3.0.0 Public Release

- **Added over 500 JUnit and integration tests**: Enhanced the reliability and robustness of the application by
  ensuring comprehensive test coverage across all key parts.
- **Rebuilt the codebase**: Refactored and optimized the existing code for improved performance, maintainability, and
  scalability.
- **Added support for refresh tokens**: Implemented support for secure refresh tokens. Both access and refresh tokens
  are now generated as `HttpOnly` cookies to enhance security and prevent client-side access.

## v 2.0.0 First Public Release

- First Public Release

## v1.0.0

- Beta App Release  

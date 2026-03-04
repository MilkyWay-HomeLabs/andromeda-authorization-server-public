# Monitoring with Prometheus and Grafana

The Andromeda Authorization Server provides built-in support for monitoring using Spring Boot Actuator, Micrometer, and Prometheus.

## Configuration

The application is configured to expose Prometheus metrics via Spring Boot Actuator.

### Dependencies

The following dependencies in `pom.xml` enable metrics collection and Prometheus exportation:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Application Properties

Metrics are configured in `src/main/resources/application.properties`:

```properties
management.endpoints.web.base-path=/api/actuator
management.endpoints.web.exposure.include=prometheus,health,info,metrics
management.endpoint.prometheus.access=unrestricted
management.endpoint.health.show-details=always
management.metrics.tags.application=${spring.application.name}
```

- **Base Path**: The actuator endpoints are available under `/api/actuator`.
- **Prometheus Endpoint**: Metrics specifically formatted for Prometheus are available at `/api/actuator/prometheus`.
- **Application Tag**: Every metric is tagged with `application="andromeda-authorization-server"` for easier filtering in multi-app environments.

## Custom Metrics

### HealthController

The application uses the `@Timed` annotation to track the performance of specific API endpoints in `org.derleta.authorization.controller.HealthController`:

- **`GET /api/v1/hello`**:
    - Metric name: `http.api.v1.health.hello`
    - Description: Time spent handling GET /api/v1/hello
    - Percentiles: 0.5, 0.95, 0.99
- **`GET /api/v1/version`**:
    - Metric name: `http.api.v1.health.version`
    - Description: Time spent handling GET /api/v1/version
    - Percentiles: 0.5, 0.95, 0.99

### AuthApiService

Custom metrics in `org.derleta.authorization.security.api.AuthApiService` for tracking authentication logic:

#### Timers (Latency)
- `auth_update_access_token_seconds`: Time spent updating an access token.
- `auth_refresh_seconds`: Time spent on the complete refresh token flow.
- `auth_refresh_db_is_active_seconds`: Time spent checking if a refresh token is active in the database.
- `auth_refresh_db_revoke_by_jti_seconds`: Time spent revoking a refresh token by JTI.
- `auth_refresh_generate_refresh_seconds`: Time spent generating a new refresh token.
- `auth_logout_seconds`: Time spent on logout.
- `auth_logout_all_seconds`: Time spent on "logout all sessions".
- `auth_token_encrypt_seconds`: Time spent encrypting tokens before storage.
- `auth_token_db_save_seconds`: Time spent saving tokens to the database.

#### Counters (Throughput & Errors)
- `auth_update_access_token_total`: Counts access token updates with tags:
    - `outcome`: `success`, `null_user_id`, `error`.
- `auth_refresh_total`: Counts refresh attempts with tags:
    - `outcome`: `success`, `blank`, `invalid`, `already_used`, `missing_session_exp`, `error`.
- `auth_logout_total`: Counts logout attempts with tags:
    - `outcome`: `success`, `blank`, `error`.
- `auth_logout_all_total`: Counts "logout all" attempts with tags:
    - `outcome`: `success`, `null_user_id`, `user_not_found`, `error`.
- `auth_token_saved_total`: Counts successful or failed token persistences with tags:
    - `token_type`: `access`, `refresh`.
    - `outcome`: `success`, `not_saved`, `error`.
- `auth_logout_all_revoked_total`: Counts total revoked tokens during "logout all" with tags:
    - `token_type`: `access`, `refresh`.

### Token Services

Metrics in `AccessTokenService`, `RefreshTokenService`, and `ConfirmationTokenService`:

- `auth_token_created_total`: Total tokens created.
    - Tag `type`: `access`, `refresh`, `confirm`.
- `auth_token_create_failed_total`: Total failed token creations.
    - Tag `type`: `access`, `refresh`, `confirm`.
- `auth_token_create_seconds`: Latency of token creation.
    - Tag `type`: `access`, `refresh`, `confirm`.

## Prometheus Query Examples

### HTTP Traffic for Andromeda

```promql
sum by (uri, method, status) (
  rate(http_server_requests_seconds_count{job="tomcat-andromeda"}[5m])
)
```

### All Apps Breakdown by Job

```promql
sum by (job) (
  rate(http_server_requests_seconds_count[5m])
)
```

### Top URIs Comparison

```promql
topk(10,
  sum by (job, uri) (rate(http_server_requests_seconds_count[5m]))
)
```

### Filter by Application Tag

```promql
sum by (application) (rate(http_server_requests_seconds_count[5m]))
```

## Grafana Integration

In Grafana, it is recommended to use variables for dynamic filtering.

### Variable Configuration

Create a variable named `$app`:
- **Query**: `label_values(http_server_requests_seconds_count, application)`

Alternatively, if the application tag is not used, use the `job` label:
- **Query**: `label_values(http_server_requests_seconds_count, job)`

### Dashboard Panels

Use the variables in your panel queries:

```promql
sum by (uri) (
  rate(http_server_requests_seconds_count{application=~"$app"}[5m])
)
```

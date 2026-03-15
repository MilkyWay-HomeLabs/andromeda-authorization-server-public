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

The application uses the `@Timed` annotation to track the performance of specific API endpoints.

### HealthController

In `org.derleta.authorization.controller.HealthController`, the following endpoints are monitored:

- **`GET /api/v1/hello`**:
    - Metric name: `http.api.v1.health.hello`
    - Description: Time spent handling GET /api/v1/hello
    - Percentiles: 0.5, 0.95, 0.99
- **`GET /api/v1/version`**:
    - Metric name: `http.api.v1.health.version`
    - Description: Time spent handling GET /api/v1/version
    - Percentiles: 0.5, 0.95, 0.99

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

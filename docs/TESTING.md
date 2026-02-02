# Testing and Coverage Report

The Andromeda Authorization Server maintains a high standard of quality through extensive automated testing. This document provides an overview of the testing strategy and current coverage status.

## Coverage Summary

The following statistics are based on the latest JaCoCo coverage report.

| Metric | Coverage % | Absolute Value |
| :--- | :--- | :--- |
| **Classes** | 98.4% | 123 / 125 |
| **Methods** | 89.5% | 570 / 637 |
| **Branches** | 73.3% | 371 / 506 |
| **Lines** | 90.2% | 1696 / 1880 |

## Package Breakdown

| Package | Class % | Method % | Branch % | Line % |
| :--- | :--- | :--- | :--- | :--- |
| `org.derleta.authorization.controller` | 100% | 100% | 85.9% | 96.8% |
| `org.derleta.authorization.service` | 100% | 100% | 87.5% | 96.2% |
| `org.derleta.authorization.repository` | 100% | 100% | 100% | 100% |
| `org.derleta.authorization.config` | 50% | 75% | - | 83.3% |
| `org.derleta.authorization.domain` | 100% | 84.1% | 43.1% | 78.4% |

## Testing Strategy

### Technologies Used
- **JUnit 5**: Core testing framework.
- **Spring Boot Test**: Integration testing with Spring context.
- **MockMvc**: For testing REST controllers without starting a full HTTP server.
- **JaCoCo**: Code coverage analysis.
- **MariaDB (Test) / H2**: Database testing.

### Test Categories
1. **Unit Tests**: Testing individual components (services, mappers, builders) in isolation.
2. **Integration Tests**: Testing the interaction between multiple layers (controller -> service -> repository).
3. **Security Tests**: Verifying JWT token generation, validation, and role-based access control.
4. **API Endpoints Tests**: Testing full request/response cycles for all public and protected endpoints.

## Running Tests

To execute the full test suite and generate a coverage report, use the following Maven command:

```bash
mvn clean test
```

The coverage report will be generated in:
`target/site/jacoco/index.html` (or `htmlReport/index.html` in the project root if pre-generated).

## CI/CD Integration
Tests are automatically executed on every push to the repository via GitHub Actions. A successful build requires all tests to pass.

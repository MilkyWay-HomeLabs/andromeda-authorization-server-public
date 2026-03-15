# Testing and Coverage Report

The Andromeda Authorization Server maintains a high standard of quality through extensive automated testing. This document provides an overview of the testing strategy and current coverage status.

## Coverage Summary

The following statistics are based on the latest JaCoCo coverage report.

| Metric | Coverage % | Absolute Value |
| :--- | :--- | :--- |
| **Classes** | 98.6% | 139 / 141 |
| **Methods** | 95.9% | 767 / 800 |
| **Branches** | 72.8% | 562 / 772 |
| **Lines** | 92.1% | 2466 / 2678 |

## Package Breakdown

| Package | Class % | Method % | Branch % | Line % |
| :--- | :--- | :--- | :--- | :--- |
| `org.derleta.authorization.controller` | 100% | 100% | 82.4% | 94.6% |
| `org.derleta.authorization.service` | 100% | 100% | 90.9% | 96.9% |
| `org.derleta.authorization.repository.impl` | 100% | 85.2% | 50% | 71.3% |
| `org.derleta.authorization.config` | 100% | 100% | - | 100% |
| `org.derleta.authorization.domain.entity` | 100% | 100% | 75.9% | 95.2% |
| `org.derleta.authorization.domain.model` | 100% | 100% | - | 100% |

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

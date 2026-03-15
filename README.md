# Andromeda Authorization Server

<p align="center">
  <a href="https://github.com/MilkyWay-HomeLabs/andromeda-authorization-server-public">
    <img alt="Repo" src="https://img.shields.io/badge/Repo-GitHub-0f172a?style=for-the-badge&logo=github&logoColor=white">
  </a>
  <img alt="Version" src="https://img.shields.io/badge/Version-4.1.0--PUBLIC-2563eb?style=for-the-badge">
  <img alt="Coverage" src="https://img.shields.io/badge/Coverage-90.2%25-16a34a?style=for-the-badge">
  <img alt="Release" src="https://img.shields.io/badge/Release-Public-16a34a?style=for-the-badge">
</p>

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/Java-21-0b3d91?style=flat-square&logo=openjdk&logoColor=white">
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-3.2-16a34a?style=flat-square&logo=springboot&logoColor=white">
  <img alt="Maven" src="https://img.shields.io/badge/Maven-Build-c71a36?style=flat-square&logo=apachemaven&logoColor=white">
  <img alt="MariaDB" src="https://img.shields.io/badge/MariaDB-Database-003545?style=flat-square&logo=mariadb&logoColor=white">
  <img alt="JWT" src="https://img.shields.io/badge/JWT-Security-111827?style=flat-square&logo=jsonwebtokens&logoColor=white">
</p>

---

## 📖 Documentation

- 📄 [Changelog](docs/CHANGELOG.md) — project history.
- 🚀 [Endpoints](docs/ENDPOINTS.md) — detailed API endpoints description.
- 📊 [Metrics](docs/METRICS.md) — metrics and monitoring information.
- 🧪 [Testing](docs/TESTING.md) — testing instructions.
- ❓ [Help](docs/HELP.md) — help and troubleshooting.
- 🛡️ [Production Ready Checklist](docs/PROD_READY_CHECKLIST.md) — security and production recommendations.

---

## 🌟 Overview

**Andromeda Authorization Server** is a robust and versatile application built with **Java SDK 21** and the advanced
**Spring Boot 3.2** framework. This server effectively manages access and authorization, prioritizing security with the
use of **JWT (JSON Web Token)** authentication.

### Key Features

- 🔐 **JWT Token Authentication**: Ensures enhanced security by implementing JSON Web Tokens, providing a reliable and
  secure authentication mechanism to safeguard applications from unauthorized access.
- 🗄️ **MariaDB Integration**: Effortlessly integrates with MariaDB using JDBC for efficient and secure management of
  access and authorization data.
- 📧 **Email Services with Google Accounts**: Comes equipped with a built-in email service utilizing Google accounts for
  convenient handling of password resets and activation link emails, simplifying the user account management experience.
- 🔄 **Jakarta EE and Spring Compatibility**: Demonstrates seamless interoperability between Jakarta EE technologies and
  Spring, combining the strengths of both frameworks for scalable, secure enterprise applications.
- 📈 **Enterprise-Level Scalability**: Leverages modern Java SDK 21 features and advanced Spring Boot capabilities to
  provide a highly scalable and maintainable solution for handling access control and user authentication in enterprise
  systems.

### Monitoring and Diagnostics

The application provides simple health and version endpoints:

- `GET /api/v1/hello` — returns a simple "hello" message.
- `GET /api/v1/version` — returns the current application version.

> [!WARNING]
> All endpoints should be verified carefully during the final stages of development to ensure security and proper
> functionality. Some of them might need to be removed or restricted for security reasons. Consider this a warning to
> review all endpoints thoroughly, especially those handling sensitive data or authentication, to avoid potential
> vulnerabilities.

---

## 🛠️ API Examples

This application supports and issues JWT-based cookies for `accessToken` and `refreshToken`.

### Token Refresh (Example)

The following example demonstrates how to use the `refreshToken` to obtain a new `accessToken`.

```http
POST /api/v1/auth/refresh-access HTTP/1.1
Host: localhost:8443
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Cookie: refreshToken=eyJhbGciOiJIUzUxMiJ9...
```

More examples in `.http` format for IntelliJ can be found in the `src/test/endpoints` directory.

---

## 🏗️ Building the Project

### Prerequisites

- **Java SDK 21**
- **Maven 3.x**
- **MariaDB** (running instance)

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/MilkyWay-HomeLabs/andromeda-authorization-server-public.git
   cd andromeda-authorization-server-public
   ```

2. **Configuration (Environment Variables)**
   Configure the necessary environment variables in your system or `.env` file:

   ```bash
   APP_JWT_SECRET=<your-secret-key>
   APP_JWT_ACCESS_EXPIRATION=3600000
   APP_JWT_REFRESH_EXPIRATION=604800000
   ALLOWED_APPS_HEADERS=nebula_rest_api
   ANDROMEDA_DB_URL=jdbc:mariadb://localhost:3306/andromeda
   ANDROMEDA_DB_USERNAME=user
   ANDROMEDA_DB_PASSWORD=pass
   AUTH_MAIL_HOST=smtp.gmail.com
   AUTH_MAIL_PORT=587
   AUTH_MAIL_USERNAME=your-email@gmail.com
   AUTH_MAIL_PASSWORD=your-app-password
   ```

3. **Build and Run**
   ```bash
   # Build the package
   mvn clean package

   # Run the application
   java -jar target/andromeda-authorization-server-4.1.0-PUBLIC.war
   ```

---

## 🚀 Verification

After starting, the server is available at `http://localhost:8080`. You can verify the setup by calling:

```bash
curl http://localhost:8080/api/v1/hello
```

---

## 📄 License

This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for the full license text and the [NOTICE](NOTICE) file for additional information.

---

## 👤 Author

**Szymon Derleta**  
GitHub: [@szymonderleta](https://github.com/szymonderleta)

## 🏠 Project

Organization: [@MilkyWay-HomeLabs](https://github.com/MilkyWay-HomeLabs)  
Repository: [Andromeda-Authorization-Server](https://github.com/MilkyWay-HomeLabs/andromeda-authorization-server-public)

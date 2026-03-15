# API Endpoints Documentation

This document provides a comprehensive list of the REST API endpoints available in the Andromeda Authorization Server.

All endpoints are prefixed with `/api/v1` unless otherwise specified.

## Table of Contents
1. [Authentication](#authentication)
2. [Account Management](#account-management)
3. [User Management](#user-management)
4. [Role Management](#role-management)
5. [User Role Management](#user-role-management)
6. [Token Management](#token-management)
7. [Token Incident Management](#token-incident-management)
8. [System Health](#system-health)
9. [REST Examples](#rest-examples)
10. [IntelliJ HTTP Client Files](#intellij-http-client-files)

---

## Authentication
**Base Path:** `/api/v1/auth`

### Login
- **URL:** `/login`
- **Method:** `POST`
- **Description:** Authenticates a user and returns JWT tokens (access and refresh) in `HttpOnly` cookies.
- **Payload:** `AuthLoginRequest` (login, password)
- **Response:** `200 OK` with user details (username, email) or `401 Unauthorized`.

#### Login Examples

**Login with username:**
```http
POST /api/v1/auth/login
Content-Type: application/json
X-Requesting-App: nebula_rest_api

{
  "login": "user",
  "password": "password"
}
```

**Login with email:**
```http
POST /api/v1/auth/login
Content-Type: application/json
X-Requesting-App: nebula_rest_api

{
  "login": "user@local.com",
  "password": "password"
}
```

### Refresh Access Token
- **URL:** `/refresh-access`
- **Method:** `POST`
- **Description:** Refreshes the access token using the refresh token from cookies.
- **Cookies Required:** `refreshToken`
- **Response:** `200 OK` with `AccessResponse` or `401 Unauthorized`.

---

## Account Management
**Base Path:** `/api/v1`

### User Registration
- **URL:** `/public/account/register`
- **Method:** `POST`
- **Description:** Registers a new user.
- **Payload:** `UserRegistrationRequest`
- **Response:** `200 OK` or `400 Bad Request` with `AccountResponse`.

### Get User Roles (Public)
- **URL:** `/public/account`
- **Method:** `GET`
- **Description:** Retrieves roles for a user by username and email.
- **Query Params:** `username`, `email`
- **Response:** `200 OK` with `UserRolesResponse`.

### Confirm Account
- **URL:** `/public/account/confirm`
- **Method:** `PATCH`
- **Description:** Confirms user registration using a token.
- **Payload:** `UserConfirmationRequest`
- **Response:** `200 OK` or `400 Bad Request`.

### Unlock Account
- **URL:** `/public/account/unlock/{id}`
- **Method:** `PATCH`
- **Description:** Unlocks a locked user account.
- **Path Variable:** `id` (User ID)
- **Response:** `200 OK` or `400 Bad Request`.

### Reset Password
- **URL:** `/public/account/reset-password`
- **Method:** `PATCH`
- **Description:** Initiates a password reset for the specified email.
- **Query Params:** `email`
- **Response:** `200 OK` or `400 Bad Request`.

### Change Password
- **URL:** `/account/change-password`
- **Method:** `POST`
- **Authorization:** `ROLE_USER`, `ROLE_ADMIN`
- **Payload:** `ChangePasswordRequest`
- **Response:** `200 OK` or `400 Bad Request`.

---

## User Management
**Base Path:** `/api/v1/table/users`

### Get Users (Paginated)
- **URL:** `/`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Query Params:** `page`, `size`, `sortBy`, `sortOrder`, `usernameFilter`, `emailFilter`
- **Response:** `200 OK` with `PagedModel<UserResponse>`.

### Get User by ID
- **URL:** `/{id}`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Response:** `200 OK` or `404 Not Found`.

### Add User
- **URL:** `/`
- **Method:** `POST`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Payload:** `User`
- **Response:** `201 Created`.

### Update User
- **URL:** `/{id}`
- **Method:** `PUT`
- **Authorization:** `ROLE_MODERATOR`, `ROLE_ADMIN`
- **Payload:** `User`
- **Response:** `200 OK` or `404 Not Found`.

### Delete User
- **URL:** `/{id}`
- **Method:** `DELETE`
- **Authorization:** `ROLE_ADMIN`
- **Response:** `200 OK` or `404 Not Found`.

---

## Role Management
**Base Path:** `/api/v1/table/roles`

### Get Roles List
- **URL:** `/list`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Query Params:** `roleNameFilter`
- **Response:** `200 OK` with `CollectionModel<RoleResponse>`.

#### Roles List Examples

**Get roles with name filter (Cookie):**
```http
GET /api/v1/table/roles/list?roleNameFilter=er
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Cookie: accessToken=...
```

**Get roles with name filter (Bearer):**
```http
GET /api/v1/table/roles/list?roleNameFilter=er
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Authorization: Bearer ...
```

### Get Roles (Paginated)
- **URL:** `/`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Query Params:** `page`, `size`, `sortBy`, `sortOrder`, `roleNameFilter`
- **Response:** `200 OK` with `PagedModel<RoleResponse>`.

### Get Role by ID
- **URL:** `/{id}`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_TESTER`, `ROLE_MODERATOR`
- **Response:** `200 OK` or `404 Not Found`.

### Add Role
- **URL:** `/`
- **Method:** `POST`
- **Authorization:** `ROLE_ADMIN`
- **Payload:** `Role`
- **Response:** `201 Created`.

### Update Role
- **URL:** `/{id}`
- **Method:** `PUT`
- **Authorization:** `ROLE_ADMIN`
- **Payload:** `Role`
- **Response:** `200 OK` or `404 Not Found`.

### Delete Role
- **URL:** `/{id}`
- **Method:** `DELETE`
- **Authorization:** `ROLE_ADMIN`
- **Response:** `200 OK` or `404 Not Found`.

---

## User Role Management
**Base Path:** `/api/v1/table`

### Get User-Role Mappings (Paginated)
- **URL:** `/user-role`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_MODERATOR`
- **Query Params:** `page`, `size`, `sortBy`, `sortOrder`, `usernameFilter`, `emailFilter`, `roleNameFilter`
- **Response:** `200 OK` with `PagedModel<UserRoleResponse>`.

### Get User-Role Mapping by ID
- **URL:** `/user-role/{id}`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_MODERATOR`
- **Response:** `200 OK` or `404 Not Found`.

### Add User-Role Association
- **URL:** `/user-role/{userId}/{roleId}`
- **Method:** `POST`
- **Authorization:** `ROLE_ADMIN`
- **Response:** `201 Created`.

### Delete User-Role Association
- **URL:** `/user-role/{userId}/{roleId}`
- **Method:** `DELETE`
- **Authorization:** `ROLE_ADMIN`
- **Response:** `200 OK` or `404 Not Found`.

### Get All Roles for a Specific User
- **URL:** `/user-roles/{userId}`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`, `ROLE_MODERATOR`
- **Query Params:** `roleNameFilter`, `sortBy`, `sortOrder`
- **Response:** `200 OK` with `UserRolesResponse`.

---

## Token Management
### Access Tokens
**Base Path:** `/api/v1/table/tokens/access`
- `GET /`: Paginated list (Admin)
- `GET /valid`: Paginated valid tokens (Admin)
- `GET /{id}`: Get by ID (Admin)
- `POST /{userId}`: Add for user (Admin)
- `DELETE /{tokenId}/{userId}`: Delete (Admin)

#### Access Tokens Examples

**Get token page:**
```http
GET /api/v1/table/tokens/access?page=0&size=5
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Cookie: accessToken=...
```

### Refresh Tokens
**Base Path:** `/api/v1/table/tokens/refresh`
- `GET /`: Paginated list (Admin)
- `GET /valid`: Paginated valid tokens (Admin)
- `GET /{id}`: Get by ID (Admin)
- `POST /{userId}`: Add for user (Admin)
- `DELETE /{tokenId}/{userId}`: Delete (Admin)

### Confirmation Tokens
**Base Path:** `/api/v1/table/tokens/confirmation`
- `GET /`: Paginated list (Admin)
- `GET /valid`: Paginated valid tokens (Admin)
- `GET /{id}`: Get by ID (Admin)
- `POST /{userId}`: Add for user (Admin)
- `DELETE /{tokenId}/{userId}`: Delete (Admin)

---

## Token Incident Management
**Base Path:** `/api/v1/table/tokens/incidents/refresh`

### Get Refresh Token Incidents (Paginated)
- **URL:** `/`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`
- **Query Params:** `page`, `size`
- **Response:** `200 OK` with `PagedModel<RefreshTokenIncidentResponse>`.

### Get Incident by ID
- **URL:** `/{id}`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`
- **Response:** `200 OK` or `404 Not Found`.

### Get Incidents From Instant
- **URL:** `/from`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`
- **Query Params:** `from` (ISO Instant), `limit`
- **Response:** `200 OK` with `PagedModel<RefreshTokenIncidentResponse>`.

### Get Last 24h Incidents
- **URL:** `/last-24h`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`
- **Query Params:** `limit`
- **Response:** `200 OK` with `PagedModel<RefreshTokenIncidentResponse>`.

### Get Incidents From Timestamp
- **URL:** `/from-timestamp`
- **Method:** `GET`
- **Authorization:** `ROLE_ADMIN`
- **Query Params:** `from` (SQL Timestamp), `limit`
- **Response:** `200 OK` with `PagedModel<RefreshTokenIncidentResponse>`.

---

## System Health
**Base Path:** `/api/v1`

### Hello
- **URL:** `/hello`
- **Method:** `GET`
- **Description:** Simple health check endpoint.
- **Response:** `200 OK` ("hello").

### Version
- **URL:** `/version`
- **Method:** `GET`
- **Description:** Returns the application version.
- **Response:** `200 OK` (version string).

---

## REST Examples
This section contains raw examples that can be used with HTTP clients (e.g., IntelliJ HTTP Client, cURL).

### Authentication
```http
### Login with username
POST /api/v1/auth/login
Content-Type: application/json
X-Requesting-App: nebula_rest_api

{
  "login": "user",
  "password": "password"
}

### Login with email
POST /api/v1/auth/login
Content-Type: application/json
X-Requesting-App: nebula_rest_api

{
  "login": "user@local.com",
  "password": "password"
}
```

### Data Retrieval
```http
### Get access tokens page
GET /api/v1/table/tokens/access?page=0&size=5
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Cookie: accessToken=...

### Get roles with filter (Bearer token)
GET /api/v1/table/roles/list?roleNameFilter=er
Content-Type: application/json
X-Requesting-App: nebula_rest_api
Authorization: Bearer ...
```

---

## IntelliJ HTTP Client Files

The project includes `.http` files that can be used directly in **IntelliJ IDEA** to test the API. These files contain example requests for all endpoints, including local development, testing, and production environments.

### Location
All files are located in: `src/test/endpoints/`

### File Structure
- `account/`: Registration, confirmation, password reset, etc.
- `auth/`: Login and token refresh
- `health/`: System health check and version
- `role/`: Role management (CRUD)
- `token/`: Access, Refresh, and Confirmation token management
- `user/`: User management (CRUD)
- `user-role/`: Managing individual user-role associations
- `user-roles/`: Retrieving roles for a specific user

### Usage
1. Open any `.http` file in IntelliJ IDEA.
2. You will see green "Run" icons (▶️) next to each request.
3. Click the icon to execute the request against `localhost`, `dev`, or `production` by selecting the corresponding URL in the file or by using IntelliJ environments.
4. Most examples are configured to run on `http://localhost:8444` by default for local development.

> [!TIP]
> Make sure the application is running locally before executing `localhost` requests.

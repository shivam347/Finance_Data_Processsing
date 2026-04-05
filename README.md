# Finance Dashboard Backend Project

> A **Backend project** for managing financial records, user roles, and dashboard analytics — built with security-first design and clean layered architecture.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| Spring Boot 3.2.x | Application framework |
| Spring Security + JWT | Authentication & Authorization |
| Spring Data JPA | ORM & database abstraction |
| MySQL 8 | Relational data persistence |
| Flyway | Database schema migrations |
| Swagger / OpenAPI | Interactive API documentation |

---

## System Architecture

The application follows a clean **layered architecture** where each layer has one clear responsibility. A request flows from top to bottom:

```
+-------------------------------------------------------------+
|                      CLIENT / FRONTEND                      |
+---------------------------+---------------------------------+
                            |  HTTP Request
+---------------------------v---------------------------------+
|                SECURITY LAYER  (JWT Filter)                 |
|            Validates token -> extracts user role            |
+---------------------------+---------------------------------+
                            |  Authenticated Request
+---------------------------v---------------------------------+
|                CONTROLLER LAYER  (API)                      |
|        Routes requests -> validates input -> calls service  |
+---------------------------+---------------------------------+
                            |  Business Call
+---------------------------v---------------------------------+
|                SERVICE LAYER  (Business Logic)              |
|     Applies rules -> orchestrates data -> calls repository  |
+---------------------------+---------------------------------+
                            |  Data Query
+---------------------------v---------------------------------+
|                REPOSITORY LAYER  (Data Access)              |
|            Spring Data JPA -> executes SQL -> MySQL         |
+---------------------------+---------------------------------+
                            |
+---------------------------v---------------------------------+
|                      MySQL 8 DATABASE                       |
+-------------------------------------------------------------+
```

---

## Setup Instructions

### 1. Database Setup
Ensure MySQL is running, then create the database (Flyway will handle the rest):
```sql
CREATE DATABASE financeRecords_db;
```
> Schema and seed data are automatically applied via Flyway migrations on startup.

### 2. Configuration
Update `src/main/resources/application-dev.properties` with your MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Build & Run
```bash
./mvnw clean install
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Exploring the API

Once the server is running, open the interactive Swagger UI:

http://localhost:8080/swagger-ui.html

### Default Admin Credentials
```
Email:    admin@dashboard.com
Password: admin123
```

---

## Project Structure

```
src/main/java/com/finance/dashboard/
|
|   FinanceDashboardApplication.java       <- Main entry point
|
+-- config/                                <- App-wide configuration
|       AppConfig.java
|       JpaConfig.java
|       SecurityConfig.java
|       SwaggerConfig.java
|
+-- security/                              <- Auth & JWT mechanics
|       JwtAuthFilter.java
|       JwtUtil.java
|       UserDetailsImpl.java
|       UserDetailsServiceImpl.java
|
+-- controller/                            <- API endpoints (entry gates)
|       AuthController.java
|       DashboardController.java
|       FinancialRecordController.java
|       UserController.java
|
+-- dto/                                   <- Data shapes (safe I/O)
|   +-- auth/
|   +-- dashboard/
|   +-- record/
|   \-- user/
|
+-- service/                               <- Business logic layer
|       AuthService.java
|       DashboardService.java
|       FinancialRecordService.java
|       UserService.java
|
+-- repository/                            <- Database access layer
|       FinancialRecordRepository.java
|       UserRepository.java
|
+-- entity/                                <- Database table models
|       FinancialRecord.java
|       User.java
|
+-- enums/                                 <- Strongly-typed constants
|       Role.java
|       TransactionType.java
|       UserStatus.java
|
+-- exception/                             <- Global error handling
|       ApiException.java
|       ErrorResponse.java
|       GlobalExceptionHandler.java
|
\-- utils/                                 <- Shared utilities
        ApiResponse.java
```

---

## Layer-by-Layer File Breakdown

### `config/` — Configuration Layer
> *Bootstraps the application's core settings before anything else runs.*

| File | Purpose |
|---|---|
| `AppConfig.java` | Registers core Spring beans like `PasswordEncoder` (BCrypt). |
| `JpaConfig.java` | Enables JPA Auditing to auto-populate `createdAt` / `updatedAt` timestamps on entities. |
| `SecurityConfig.java` | Defines which endpoints are public vs protected, configures CORS rules, and wires in the JWT filter. |
| `SwaggerConfig.java` | Sets up the OpenAPI/Swagger UI with JWT bearer token support for interactive docs. |

---

### `security/` — Authentication Layer
> *Intercepts every request, validates the JWT, and tells Spring Security who the user is.*

| File | Purpose |
|---|---|
| `JwtAuthFilter.java` | Runs before every request — reads the `Authorization` header, validates the token, and sets the security context. |
| `JwtUtil.java` | Generates JWT tokens on login and parses/validates them on each subsequent request. |
| `UserDetailsImpl.java` | Wraps the `User` entity into the `UserDetails` interface that Spring Security natively understands. |
| `UserDetailsServiceImpl.java` | Loads user details from the database by email during the authentication handshake. |

---

### `controller/` — API Layer
> *The HTTP entry gates — route incoming requests and return clean JSON responses.*

| File | Purpose |
|---|---|
| `AuthController.java` | Handles public `/auth/login` and `/auth/register` endpoints. No token needed. |
| `DashboardController.java` | Serves analytics endpoints — category totals, monthly trends, and summary cards. |
| `FinancialRecordController.java` | Full CRUD endpoints for financial records, gated by user role. |
| `UserController.java` | Handles profile retrieval and admin user-management (update role/status). |

---

### `dto/` — Data Transfer Objects
> *Controls exactly what data enters and exits the API — no raw entity exposure.*

| Subfolder | Files & Purpose |
|---|---|
| `auth/` | `LoginRequest` (credentials in), `RegisterRequest` (signup payload), `AuthResponse` (JWT out). |
| `dashboard/` | `SummaryResponse`, `CategoryTotalResponse`, `MonthlyTrendResponse` — structured analytics payloads. |
| `record/` | `CreateRecordRequest`, `UpdateRecordRequest` (data in); `RecordResponse` (safe data out). |
| `user/` | `UpdateUserRequest` (role/status changes); `UserResponse` (profile data, password excluded). |

---

### `service/` — Business Logic Layer
> *The brain of the application — all rules, validations, and orchestration live here.*

| File | Purpose |
|---|---|
| `AuthService.java` | Hashes passwords on registration, validates credentials on login, and issues JWTs. |
| `DashboardService.java` | Queries repositories and aggregates data into summary metrics and trend calculations. |
| `FinancialRecordService.java` | Enforces ownership and role rules before creating, updating, or soft-deleting records. |
| `UserService.java` | Handles profile lookups and enforces safety constraints (e.g. admins cannot modify their own role). |

---

### `repository/` — Data Access Layer
> *Speaks directly to MySQL using Spring Data JPA — no boilerplate SQL needed.*

| File | Purpose |
|---|---|
| `FinancialRecordRepository.java` | Provides queries like `findByUserId`, `findByTypeAndUserId`, and aggregation methods. |
| `UserRepository.java` | Provides `findByEmail`, `existsByEmail`, and other user-lookup methods. |

---

### `entity/` — Domain Models
> *Java representations of the MySQL database tables.*

| File | Purpose |
|---|---|
| `User.java` | Maps to the `users` table — stores credentials, role, and status with JPA auditing. |
| `FinancialRecord.java` | Maps to the `financial_records` table — stores amounts in paise (integer) for precision. |

---

### `enums/` — Type Safety
> *Restricts fields to valid values only — prevents data inconsistency at compile time.*

| File | Purpose |
|---|---|
| `Role.java` | Defines `ADMIN`, `ANALYST`, `VIEWER` — drives all access control decisions. |
| `TransactionType.java` | Restricts record types to `INCOME` or `EXPENSE`. |
| `UserStatus.java` | Limits user state to `ACTIVE`, `INACTIVE`, or `SUSPENDED`. |

---

### `exception/` — Global Error Handling
> *Catches all failures and returns clean, structured error responses instead of raw stack traces.*

| File | Purpose |
|---|---|
| `ApiException.java` | Custom runtime exception thrown intentionally for business rule violations (e.g., "Record not found"). |
| `ErrorResponse.java` | The uniform JSON error payload template sent back to the client. |
| `GlobalExceptionHandler.java` | A `@ControllerAdvice` that intercepts all exceptions app-wide and maps them to `ErrorResponse`. |

---

### `utils/` — Shared Utilities
> *Reusable helpers used across the entire application.*

| File | Purpose |
|---|---|
| `ApiResponse.java` | A generic wrapper ensuring every successful response has a standard shape: `{ status, message, data }`. |



---

## Role-Based Access Control (RBAC)

| Role | Permissions |
|---|---|
| VIEWER | Login, view own profile, view own financial records, view own dashboard |
| ANALYST | All VIEWER access + view all users' records and summaries + view monthly trends |
| ADMIN | Full system control — create, update, soft-delete any record, manage user roles and status |

> **Note:** Admins cannot modify their own role or status for security reasons.

# University Club Management System --- Backend

Backend for a university club management platform. The application
provides identity and security, membership and access control, event
management, and club inventory management through a versioned REST API.

## Architecture

The backend is structured as a **modular monolith**:

-   One Spring Boot application is deployed and run as a single service.
-   Domain functionality is separated into modules to keep
    responsibilities organized.
-   Modules share the same application runtime and PostgreSQL database.
-   The modular structure leaves room for future extraction of
    independently deployable services if the project grows.

The system is designed for one university club. It is not currently a
multi-tenant or microservices deployment.

## Technology stack

  Technology          Purpose
  ------------------- ----------------------------------
  Java 21             Application runtime
  Spring Boot 4.1.2   Backend framework
  Spring Security     Authentication and authorization
  JWT                 Stateless API authentication
  BCrypt              Password hashing
  PostgreSQL 16       Relational database
  Flyway              Database schema migrations
  Maven               Build and dependency management

Versions and configuration may differ in later commits. Check the
project's build file and application configuration for the definitive
values.

## Implemented functional areas

### Identity and security

-   User registration and login.
-   Password hashing with BCrypt.
-   JWT-based authentication.
-   Stateless Spring Security configuration.
-   Authenticated-user information.
-   Role- and permission-based authorization.

### Membership and access control

-   Club membership and membership-related roles.
-   Role-based access control (RBAC).
-   Permissions used to authorize protected operations.
-   A bootstrap mechanism for the club president, when configured.

A newly registered account's access is determined by the backend's
current implementation. Do not assume that registration automatically
assigns a particular club role. The backend remains the source of truth
for roles and permissions.

### Events

-   Event creation and management.
-   Public event listing and event details.
-   Event registration.
-   Attendance management.
-   Event-related dashboard and query functionality.

### Inventory

-   Inventory assets and categories.
-   Borrowing workflows.
-   Maintenance tracking.
-   Inventory dashboard functionality.

For exact routes, request methods, DTO fields, and permission
requirements, consult the controllers, DTOs, and security configuration
in the source code. This README intentionally does not guess
undocumented endpoint contracts.

## API

The local API base URL is:

``` text
http://localhost:8080/api/v1
```

Known authentication and account routes include:

  Method   Endpoint           Purpose
  -------- ------------------ -----------------------------------------------
  `POST`   `/auth/register`   Register an account
  `POST`   `/auth/login`      Authenticate and obtain a JWT
  `GET`    `/users/me`        Retrieve the authenticated user's information

Protected requests use the bearer-token scheme:

``` http
Authorization: Bearer <access-token>
```

The frontend should use the token field returned by the actual login
response. Check the authentication response DTO rather than assuming a
particular JSON property name.

The configured JWT lifetime has been 15 minutes in the current
implementation. Confirm the active security configuration if this value
changes.

## Prerequisites

Before starting the application, install:

-   Java 21.
-   PostgreSQL 16, or a compatible PostgreSQL version.
-   Maven, unless the repository includes and you use the Maven Wrapper.
-   Git, if cloning the repository.

## Database setup

1.  Start PostgreSQL.
2.  Create a database for the application:

``` sql
CREATE DATABASE university_club;
```

3.  Configure the database connection in the application's existing
    configuration file or environment-specific configuration.

The database URL used during development is:

``` text
jdbc:postgresql://localhost:5432/university_club
```

Set the database username and password to match your local PostgreSQL
installation. This README does not prescribe credentials: use the values
already expected by your `application.properties`, `application.yml`,
active profile, or environment configuration.

### Database migrations

Flyway manages the database schema. Pending migrations are applied
during application startup when Flyway is enabled in the active
configuration.

Do not manually recreate tables that are already managed by migrations.
When changing the schema, add a new migration using the naming
convention already present in the project's migration directory.

## Configuration

Review the backend's application configuration before running it. In
particular, verify:

-   PostgreSQL JDBC URL.
-   Database username and password.
-   JWT signing secret and token settings.
-   Server port.
-   CORS allowed origins.
-   Any optional bootstrap account settings.

Use the exact property names already defined by the project. They are
not listed here because the configuration keys and environment-variable
names must match the source code.

**Security:** never commit real database passwords, JWT signing secrets,
or other credentials. Keep local secrets outside version control and
provide deployment-specific values through the configuration mechanism
used by the project.

## Run locally

From the backend project directory:

### Using the Maven Wrapper

If the repository contains `mvnw`:

``` bash
./mvnw spring-boot:run
```

On Windows:

``` bat
mvnw.cmd spring-boot:run
```

### Using installed Maven

If Maven is installed and the project does not use the wrapper:

``` bash
mvn spring-boot:run
```

The application is expected to listen on port `8080` with the current
local configuration. Check the startup logs for the actual port and
confirm that the database connection and Flyway migrations complete
successfully.

## Authentication and authorization model

1.  A user registers or logs in through the authentication API.
2.  The backend verifies credentials and issues a JWT after successful
    authentication.
3.  The client sends the token in the `Authorization` header for
    protected requests.
4.  Spring Security validates the token and establishes the
    authenticated principal for the request.
5.  Backend authorization rules evaluate the user's roles and
    permissions.

The frontend may hide or show controls based on the authenticated user's
information, but this is only a user-interface convenience. **All access
control must be enforced by the backend.** A hidden button is not a
security boundary.

A `401 Unauthorized` response generally indicates missing or invalid
authentication. A `403 Forbidden` response generally indicates that the
authenticated user does not have permission to perform the operation.

## Frontend integration

The browser-based frontend can call the API at:

``` text
http://localhost:8080/api/v1
```

When frontend and backend run on different origins, the backend's CORS
configuration must allow the frontend origin. For local development, use
the actual origin shown in the browser address bar (for example,
`http://localhost:5500`) and ensure it matches the backend
configuration.

Do not use `0.0.0.0` as the browser origin. It is commonly used as a
server bind address; open the frontend using `localhost` or the host
name configured for your environment.

## Testing and API inspection

-   Run the project's existing tests with:

``` bash
./mvnw test
```

Or, if using installed Maven:

``` bash
mvn test
```

-   Inspect controller classes for the current API routes and HTTP
    methods.
-   Inspect request and response DTOs for the exact JSON contracts.
-   Inspect the security configuration and permission definitions for
    access requirements.
-   If OpenAPI/Swagger is configured in the project, use its generated
    API documentation; its availability is configuration-dependent.

## Troubleshooting

  -----------------------------------------------------------------------
  Symptom                             What to check
  ----------------------------------- -----------------------------------
  Application cannot connect to       Confirm PostgreSQL is running, the
  PostgreSQL                          database exists, and the configured
                                      URL, username, and password are
                                      correct.

  Flyway migration fails              Read the first migration error in
                                      the startup log. Check migration
                                      names, SQL syntax, and whether the
                                      database schema was modified
                                      manually.

  `401 Unauthorized`                  Check that login succeeded, the
                                      client is sending the current JWT,
                                      and the token has not expired.

  `403 Forbidden`                     Check the authenticated user's
                                      roles and permissions and the
                                      endpoint's authorization rule.

  Browser reports a CORS error        Check the frontend origin against
                                      the backend's allowed-origin
                                      configuration.

  Registration returns                Inspect the response body and
  `400 Bad Request`                   registration DTO validation rules.
                                      Ensure required fields and formats
                                      match the backend contract.

  Application starts on an unexpected Check the active Spring
  port                                configuration and startup logs.
  -----------------------------------------------------------------------

## Project structure

The application is organized by functional responsibility. The main
areas include identity/security, membership and access control, events,
inventory, and shared/common infrastructure.

For the exact package and directory tree, use the current repository
source as the reference. The structure may evolve as modules are
developed.

## Contributing

1.  Create a branch for your change.
2.  Keep changes within the relevant domain module where possible.
3.  Add or update database migrations for schema changes.
4.  Enforce authorization in the backend, not only in the frontend.
5.  Run the available tests before submitting a change.
6.  Do not commit credentials or local environment files containing
    secrets.

## License


# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=WalletApplicationTests

# Run a single test method
./mvnw test -Dtest=WalletApplicationTests#contextLoads
```

## Architecture

Spring Boot 4.0.5 REST API backed by PostgreSQL. Base package: `astoppello.wallet`.

**Key dependencies:**
- **Spring Data JPA** — persistence layer via JPA repositories
- **Spring Data REST** — auto-exposes JPA repositories as hypermedia REST endpoints (HAL)
- **Spring Web MVC** — for custom controllers alongside auto-exposed REST repositories
- **Lombok** — reduces boilerplate (annotations processed at compile time; excluded from the final artifact)
- **PostgreSQL** — runtime database driver

**Database:** PostgreSQL connection must be configured in `src/main/resources/application.properties` (datasource URL, username, password) before the application or integration tests can start — the `application.properties` is currently empty aside from the app name.

The project is at an early scaffolding stage; domain entities, repositories, and controllers are yet to be added under `src/main/java/astoppello/wallet/`.
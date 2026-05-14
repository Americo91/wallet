# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
./mvnw clean package

# Run the application (requires PostgreSQL running via docker-compose)
./mvnw spring-boot:run

# Start PostgreSQL
docker-compose up -d

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=WalletApplicationTests

# Run a single test method
./mvnw test -Dtest=WalletApplicationTests#contextLoads
```

## Architecture

Spring Boot 4.0.5 REST API for personal finance management. Java 21, PostgreSQL 18, Maven. Base package: `astoppello.wallet`.

**Key dependencies:** Spring Data JPA, Spring Data REST (HAL), Spring Web MVC, MapStruct (DTO mapping), Lombok, Apache Commons Lang3/Collections4.

**Database:** PostgreSQL on `localhost:5432/wallet` (postgres/postgres). Docker Compose provided. Schema is auto-created (`ddl-auto=create`) and seeded from `src/main/resources/data/*.sql` (categories, labels, sample transactions). Tests use H2 in-memory DB.

### Domain Model

All entities use UUID primary keys and embed `TrackingDate` (createdAt/updatedAt audit fields).

- **Institution** — financial institution; has many Accounts
- **Account** — bank/investment account with balance (BigDecimal), currency (EUR/USD/JPY), type (LIQUIDITY/SAVINGS/INVESTMENTS); has many Transactions
- **Transaction** — expense or income entry; belongs to Account and Category; has many Labels. Balance is auto-adjusted on create/update/delete
- **Category** — hierarchical (self-referential parent/child); typed as EXPENSE, INCOME, or TRANSFER
- **Label** — tags for transactions (many-to-many)

### Layered Structure

Controllers (`/controller`, REST at `/api/v1/`) -> Services (`/service/impl`) -> Repositories (`/repository`). DTOs in `/dto`, MapStruct mappers in `/mapper`.

**Transfer mechanism:** `TransactionService.transfer()` creates paired EXPENSE + INCOME transactions across two accounts.

### Data Import (Bootstrap)

`RecordLoader` (CommandLineRunner) loads transaction history on startup:
1. Creates Institution + Account(s)
2. `FileService.loadTransactions()` reads JSON from `src/main/resources/jsonLoad/*.json`
3. `WalletExportMapper` normalizes legacy category names (180+ mappings) and filters labels
4. Transactions are persisted via `TransactionService`

The JSON files follow the `WalletExportJson` format (see `bootstrap/walletexport/` DTOs).

### Error Handling

`MvcExceptionHandler` handles `NotFoundException`, `ConstraintViolationException`, and validation errors globally, returning 400 with structured error responses.
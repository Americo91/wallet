# Wallet

Personal finance REST API built with Spring Boot. Tracks institutions, accounts, transactions, categories, labels, goals, and standing orders (recurring transactions with scheduled execution and email notifications).

## Tech stack

- Java 21
- Spring Boot 4.0.5 (Web MVC, Data JPA, Data REST, Validation, Mail)
- PostgreSQL 18
- MapStruct · Lombok · Apache Commons
- H2 (tests)

## Prerequisites

- Java 21+
- Docker (for PostgreSQL via Docker Compose)
- Maven (wrapper included)

## Running locally

```bash
# Start PostgreSQL
docker-compose up -d

# Build and run
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`. The schema is created automatically on startup and seeded with categories and labels from `src/main/resources/data/`.

### Environment variables

| Variable | Description |
|---|---|
| `MAIL_USERNAME` | SMTP username (Gmail by default) |
| `MAIL_PASSWORD` | SMTP app password |
| `NOTIFICATION_EMAIL` | Recipient for standing order notifications |

Mail and notifications are optional. The app runs without them; standing order execution still works, only the email step is skipped.

## Configuration

Key settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wallet
spring.datasource.username=postgres
spring.datasource.password=postgres

# Standing order email notifications
wallet.notification.enabled=true
wallet.notification.days-ahead=3
wallet.notification.recipient=${NOTIFICATION_EMAIL:}
```

## Building and testing

```bash
# Build
./mvnw clean package

# All tests (H2 in-memory, no Docker needed)
./mvnw test

# Single test class
./mvnw test -Dtest=StandingOrderExecutorIT

# Single test method
./mvnw test -Dtest=StandingOrderExecutorIT#executeStandingOrders_expenseOrder_createsTransactionAndDeductsBalance
```

## Domain model

All entities use UUID primary keys and carry `createdAt`/`updatedAt` audit fields.

```
Institution
  └── Account (balance, currency, type)
        └── Transaction (amount, type, date, category, labels)

Category (hierarchical, typed: EXPENSE | INCOME | TRANSFER)
Label (many-to-many with Transaction and StandingOrder)
Goal (savings target with amount)

StandingOrder (recurring rule → generates Transactions on schedule)
```

**Account types:** `LIQUIDITY`, `SAVINGS`, `INVESTMENTS`

**Currencies:** `EUR`, `USD`, `JPY`

**Standing order frequencies:** `DAILY`, `WEEKLY`, `MONTHLY`, `YEARLY`, `UNA_TANTUM`

## API reference

Base path: `/api/v1`

### Institutions

| Method | Path | Description |
|---|---|---|
| `GET` | `/institutions/` | List all |
| `GET` | `/institutions/{id}` | Get by ID |
| `GET` | `/institutions?name=` | Get by name |
| `POST` | `/institutions/` | Create |
| `PUT` | `/institutions/{id}` | Update |
| `DELETE` | `/institutions/{id}` | Delete |

### Accounts

| Method | Path | Description |
|---|---|---|
| `GET` | `/accounts/` | List all |
| `GET` | `/accounts/{id}` | Get by ID |
| `GET` | `/accounts?name=` | Get by name |
| `POST` | `/institutions/{institutionId}/accounts/` | Create under institution |
| `PUT` | `/accounts/{id}` | Update |
| `DELETE` | `/accounts/{id}` | Delete |

### Transactions

| Method | Path | Description |
|---|---|---|
| `GET` | `/transactions/` | List all |
| `GET` | `/transactions/{id}` | Get by ID |
| `GET` | `/accounts/{accountId}/transactions` | List by account |
| `POST` | `/accounts/{accountId}/transactions/` | Create on account |
| `POST` | `/accounts/{from}/transfer/{to}` | Transfer between accounts |
| `PUT` | `/transactions/{id}` | Update |
| `DELETE` | `/transactions/{id}` | Delete |

Account balance is adjusted automatically on every create, update, and delete.

### Categories

| Method | Path | Description |
|---|---|---|
| `GET` | `/categories/` | List all |
| `GET` | `/categories/{id}` | Get by ID |
| `GET` | `/categories?name=` | Search by name |
| `POST` | `/categories/` | Create |
| `PUT` | `/categories/{id}` | Update |
| `DELETE` | `/categories/{id}` | Delete |

### Labels

| Method | Path | Description |
|---|---|---|
| `GET` | `/labels/` | List all |
| `GET` | `/labels/{id}` | Get by ID |
| `POST` | `/labels/` | Create |
| `PUT` | `/labels/{id}` | Update |
| `DELETE` | `/labels/{id}` | Delete |

### Goals

| Method | Path | Description |
|---|---|---|
| `GET` | `/goals/` | List all |
| `GET` | `/goals/{id}` | Get by ID |
| `GET` | `/goals?name=` | Get by name |
| `POST` | `/goals/` | Create |
| `PUT` | `/goals/{id}` | Update |
| `DELETE` | `/goals/{id}` | Delete |

### Standing orders

| Method | Path | Description |
|---|---|---|
| `GET` | `/standing-orders/` | List all |
| `GET` | `/standing-orders/{id}` | Get by ID |
| `GET` | `/standing-orders/upcoming?days=3` | List due within N days |
| `POST` | `/accounts/{accountId}/standing-orders/` | Create on account |
| `PUT` | `/standing-orders/{id}` | Update |
| `DELETE` | `/standing-orders/{id}` | Delete |

## Scheduled jobs

**Standing order executor** — runs daily at 01:00. Finds all enabled standing orders due today, creates the corresponding transaction, adjusts the account balance, and advances `nextOccurrence` to the next date according to the frequency. `UNA_TANTUM` orders are disabled after a single execution.

**Notification job** — runs daily at 08:00. Sends an HTML email listing all enabled standing orders due within the configured `days-ahead` window. Requires `wallet.notification.enabled=true` and a non-empty recipient address.

## Data import

On startup, `RecordLoader` optionally loads historical transaction data from JSON files placed in `src/main/resources/jsonLoad/`. The files follow the `WalletExportJson` format. Category names are normalised through a 180+ entry mapping table before import.

## Project structure

```
src/main/java/astoppello/wallet/
├── controller/       REST controllers
├── domain/           JPA entities
├── dto/              Request/response DTOs
├── mapper/           MapStruct mappers
├── repository/       Spring Data JPA repositories
├── service/          Service interfaces
│   └── impl/         Service implementations + scheduled jobs
├── bootstrap/        Startup data loader
├── exception/        Exception types and MVC handler
└── model/            Enums (Currency, Frequency, …)
```

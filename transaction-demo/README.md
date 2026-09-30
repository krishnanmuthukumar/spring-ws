# Spring Transactions Demo

A small Spring Boot project for understanding database transactions with Spring Data JPA.

The application simulates transferring money between two bank accounts. It demonstrates why a transfer should be atomic: either both account balances are updated, or neither update is persisted.

## Technologies

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven

## Project Structure

- `Account` - JPA entity representing a bank account
- `AccountRepository` - Repository for accessing accounts
- `TransferService` - Business logic for transferring money
- `AuditService` - Persists transfer audit information using `REQUIRED` and `REQUIRES_NEW` propagation
- `JdbcTransferService` - Demonstrates JDBC transfers with nested audit savepoints
- `AuditJdbcService` - Writes audit rows with `JdbcTemplate` and `NESTED` propagation
- `AccountController` - REST endpoint for transfers
- `InsufficientBalanceException` - Exception raised when an account has insufficient funds

## Database Setup

Start PostgreSQL with Docker:

```bash
docker compose up -d
```

The Docker configuration creates:

- Database: `my_database`
- Username: `postgres`
- Password: `mysecretpassword`
- Port: `5432`

The current `application.yml` uses database `postgres` and password `postgres`. Update either the Docker configuration or the application configuration so that the values match before starting the application.

## Run the Application

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The application runs at `http://localhost:8080`.

## Transfer Money

Send a POST request to transfer money between two accounts:

```bash
curl -X POST "http://localhost:8080/api/transfer?fromAccount=ACC-100&toAccount=ACC-200&amount=200.00"
```

A successful request returns:

```text
Transfer successful
```

## Run Tests

Linux/macOS:

```bash
./mvnw test
```

Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Transaction Scenarios

`TransferService` contains focused methods for comparing Spring transaction behavior:

| Method | Configuration | Expected result |
| --- | --- | --- |
| `transfer()` | Default `@Transactional` | A checked exception does not automatically trigger rollback. |
| `transferWithRollbackFor()` | `rollbackFor = InsufficientBalanceException.class` | Both account updates roll back for the checked exception. |
| `transferWithNoRollbackFor()` | `noRollbackFor = RuntimeException.class` | Both account updates remain committed after the runtime exception. |
| `transferAndFail()` | Default `@Transactional` | Both account updates roll back after the runtime exception. |
| `transferWithRequiredAudit()` | Audit uses `propagation = Propagation.REQUIRED` | The forced transfer failure rolls back both account updates and the audit record. |
| `transferWithRequiresNewAudit()` | Audit uses `propagation = Propagation.REQUIRES_NEW` | The audit method's deliberate exception rolls back its insert; the transfer catches it and commits the account updates. |
| `JdbcTransferService.transfer()` | JDBC outer transaction catches the nested audit failure | Account changes commit; the nested audit insert rolls back to its savepoint. |
| `JdbcTransferService.transferWithoutCatchingAuditFailure()` | JDBC outer transaction lets the nested audit failure escape | The audit insert rolls back to its savepoint, then the outer transaction rolls back the account changes. |

`AuditService.audit()` saves an `AuditLog` record in the `audit_log` table using `Propagation.REQUIRED`. `AuditService.auditWithRequiresNew()` saves in an independent transaction and then deliberately throws; this demonstrates that the audit transaction rolls back independently while the caller can catch the exception and commit its own transaction. Both the transfer method and the audit method log whether a transaction is active using `TransactionSynchronizationManager`.

`JdbcTransferService` uses `JdbcTemplate` for both account updates and uses the `jdbcTransactionManager` for its outer transaction. `AuditJdbcService.auditAndFail()` also uses that manager with `Propagation.NESTED`, so its insert is protected by a JDBC savepoint. The JPA transaction manager remains primary for the JPA services; the JDBC examples explicitly select `jdbcTransactionManager`.

The integration tests in `TransferServiceTest` verify the expected account balances and audit rows for each rollback or commit scenario.

## Example Transfer

For an initial state of:

| Account | Balance |
| --- | ---: |
| ACC-100 | 1000.00 |
| ACC-200 | 500.00 |

Transferring `200.00` results in:

| Account | Balance |
| --- | ---: |
| ACC-100 | 800.00 |
| ACC-200 | 700.00 |

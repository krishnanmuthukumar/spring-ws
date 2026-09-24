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

## Transaction Exercise

The transaction boundary belongs on `TransferService.transfer()`:

```java
@Transactional
public void transfer(...) {
    // debit source account
    // credit destination account
}
```

The `@Transactional` annotation is currently commented out intentionally. The service also throws an exception after saving the debit account. This makes it possible to compare the behavior of a transfer with and without a transaction:

- Without a transaction, the debit may be persisted before the failure.
- With `@Transactional`, the debit and credit should roll back together when an unchecked exception occurs.

Uncomment `@Transactional` in `TransferService` and run the tests again to study rollback behavior.

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

# Level 2: SLF4J API

This module records a sequence of experiments with SLF4J: using the API by itself, adding a logging provider, changing the log threshold, and logging an exception. `OrderService` logs messages at the `TRACE`, `DEBUG`, `INFO`, `WARN`, and `ERROR` levels.

## Exercise 1: SLF4J API only

Start with only `slf4j-api` in `pom.xml`:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>2.0.17</version>
</dependency>
```

The code compiles and can call the SLF4J `Logger` methods, but the API is only a logging facade. It does not implement log output, so without a provider the application's log messages are not printed. SLF4J 2.x may also report that no provider was found.

## Exercise 2: Add `slf4j-simple`

Add `slf4j-simple` as a runtime dependency. The current `pom.xml` contains both dependencies at version 2.0.17:

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4j.version}</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>${slf4j.version}</version>
    <scope>runtime</scope>
</dependency>
```

`slf4j-simple` is the provider that writes log messages to the console. Its default threshold is INFO, so the `TRACE` and `DEBUG` calls are filtered out.

## Exercise 3: Show DEBUG messages

Set this VM argument in the application's run configuration:

```text
-Dorg.slf4j.simpleLogger.defaultLogLevel=debug
```

This sets the simple logger's threshold to DEBUG. The DEBUG message now appears along with INFO, WARN, and ERROR; TRACE remains filtered out because it is below DEBUG.

## Exercise 4: Show TRACE messages

Change the VM argument to:

```text
-Dorg.slf4j.simpleLogger.defaultLogLevel=trace
```

TRACE is the lowest level used by this demo, so setting the threshold to TRACE displays all five messages: TRACE, DEBUG, INFO, WARN, and ERROR.

## Exercise 5: Log an exception and its stack trace

Pass the caught exception as the second argument to the SLF4J `error` method:

```java
try {
    throw new RuntimeException("Database unavailable");
} catch (Exception e) {
    logger.error("Order processing failed", e);
}
```

With the threshold set to TRACE, the output includes the error message followed by the exception type, message, and stack trace:

```text
[com.example.observability.OrderService.main()] ERROR com.example.observability.OrderService - Order processing failed
java.lang.RuntimeException: Database unavailable
    at com.example.observability.OrderService.main(OrderService.java:17)
    ...
```

Passing `e` separately lets the logging provider recognize it as a `Throwable` and print the stack trace. If only the message were logged, the exception details and call location would be missing.

## Run

From this directory, using PowerShell:

```powershell
mvn exec:java '-Dexec.mainClass=com.example.observability.OrderService'
```

The Maven Exec Plugin compiles and runs `OrderService`. With `slf4j-simple`'s default INFO threshold, the output is:

```text
[com.example.observability.OrderService.main()] INFO com.example.observability.OrderService - Order ORD-1001 received
[com.example.observability.OrderService.main()] WARN com.example.observability.OrderService - Payment for order ORD-1001 is taking longer than expected
[com.example.observability.OrderService.main()] ERROR com.example.observability.OrderService - Could not save order ORD-1001
```

To include the lower levels, configure `org.slf4j.simpleLogger.defaultLogLevel` as a system property with `debug` or `trace` before running.

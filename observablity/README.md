# Observability

This repository is a set of small exercises that build from core Java logging toward production observability. This README covers the first exercise: plain Java logging with `java.util.logging` (JUL).

## Level 1: Core Java Logging

Source: [`level-1-core-java/PlainJavaLoggingDemo.java`](level-1-core-java/PlainJavaLoggingDemo.java)

The demo uses two named loggers (`order-service` and `payment-service`), a `ConsoleHandler`, a `FileHandler`, and a custom `Formatter`. Both loggers and handlers are set to `Level.ALL` so the baseline demonstrates every message without filtering.

### Run

From the repository root with JDK 11 or later:

```powershell
javac -d level-1-core-java/out level-1-core-java/PlainJavaLoggingDemo.java
java -cp level-1-core-java/out PlainJavaLoggingDemo
```

The demo prints the absolute log-file path when it starts. It writes to `level-1-core-java/order-service.log`, creating the directory if needed. The `FileHandler` is configured to append, so each run adds more records to that file.

### Scenarios Demonstrated

| Scenario | What to look for |
| --- | --- |
| Log levels | The order logger writes `FINE`, `INFO`, `WARNING`, and `SEVERE`; the payment logger writes `INFO`. |
| Multiple loggers | Each record includes its logger name, so order and payment events can be distinguished. |
| Console output | The shared console handler receives records from both loggers. |
| File output | The shared file handler writes records from both loggers to the same log file. |
| Custom formatting | Each line contains the level, logger name, and message. |
| Repeated runs | Existing file contents are retained because append mode is enabled. |

### Try These Filtering Scenarios

Change the thresholds in `PlainJavaLoggingDemo.java`, rerun the program, and compare the console with the log file. A record must pass the logger's level first, then the level of each handler.

| Change | Expected result |
| --- | --- |
| Set both logger levels to `Level.WARNING`; leave both handlers at `Level.ALL` | `FINE` and `INFO` are discarded by the loggers. Only `WARNING` and `SEVERE` records reach either handler. |
| Leave logger levels at `Level.ALL`; set the console handler to `Level.WARNING` and the file handler to `Level.ALL` | The console shows only `WARNING` and `SEVERE`. The file contains all records from both loggers. |
| Leave logger levels at `Level.ALL`; set both handler levels to `Level.WARNING` | The loggers create all records, but both handlers publish only `WARNING` and `SEVERE`. |

Restore `Level.ALL` after experimenting to return to the baseline. The file handler appends across runs; delete `level-1-core-java/order-service.log` before a run if you want to inspect only that run's output.

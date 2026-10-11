# Level 3: Logback

This Maven project demonstrates SLF4J logging with Logback, a custom console appender and encoder, sensitive-data filtering, and a separate rolling-file configuration.

## Project files

| File | Purpose |
| --- | --- |
| `src/main/java/com/example/logging/Application.java` | Emits sample INFO, WARN, and ERROR events, including sensitive-data examples and 500 rollover test messages. |
| `src/main/java/com/example/logging/MyAppender.java` | Custom appender that writes encoded logging events to the console. |
| `src/main/java/com/example/logging/MyAppenderWithEncoder.java` | Custom appender that delegates event formatting to `MyEncoder`. |
| `src/main/java/com/example/logging/MyEncoder.java` | Formats logging events and supplies encoder header and footer bytes. |
| `src/main/java/com/example/logging/SensitiveDataFilter.java` | Denies messages containing `password`, `token`, or `secret`. |
| `src/main/resources/logback.xml` | Default configuration for the custom console appender and sensitive-data filter. |
| `src/main/resources/logback-rolling.xml` | Alternative configuration for rolling plain-text log files. |
| `pom.xml` | Defines the Java 11 Maven project and its SLF4J and Logback dependencies. |

## Default console configuration

The default `logback.xml` attaches `MyAppenderWithEncoder` to the INFO-level root logger and installs `SensitiveDataFilter`. The filter denies messages containing `password`, `token`, or `secret`; accepted events are formatted by `MyEncoder` and written to the console.

Run from this project directory to use the default configuration:

```powershell
mvn exec:java '-Dexec.mainClass=com.example.logging.Application'
```

## Rolling-file configuration

`logback-rolling.xml` is an alternative configuration. It writes to `logs/application.log` and uses `SizeAndTimeBasedRollingPolicy`:

- A rollover is triggered when the active file reaches 5 KB.
- Rolled files are uncompressed and named with the date and an index, for example `application.2026-10-11.0.log`.
- `maxHistory` is 3; with a daily date pattern, Logback retains archives for three days and removes older archives.
- Multiple indexed files can be created during the same day as the size limit is reached repeatedly.
- The rolling configuration uses Logback's standard file appender and writes each message with `%msg%n`.

Select it explicitly when running the application:

```powershell
mvn '-Dlogback.configurationFile=src/main/resources/logback-rolling.xml' exec:java '-Dexec.mainClass=com.example.logging.Application'
```

`Application` emits 500 rollover test messages so the 5 KB threshold is crossed during a run. Check the `logs` directory for the active file and date-indexed archives. The rolling configuration does not include the sensitive-data filter from the default console configuration.

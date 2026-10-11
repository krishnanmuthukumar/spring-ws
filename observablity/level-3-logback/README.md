# Level 3: Logback

This Maven project demonstrates SLF4J logging with Logback, a custom console appender and encoder, sensitive-data filtering, and a separate rolling-file configuration.

The sections below keep a record of the experiments in this module and how they fit together. The default console setup and rolling-file setup are separate configurations that can be selected independently.

## Project files

| File | Purpose |
| --- | --- |
| `src/main/java/com/example/logging/Application.java` | Emits sample INFO, WARN, and ERROR events, including sensitive-data examples and 500 rollover test messages. |
| `src/main/java/com/example/logging/MyAppender.java` | Custom appender that writes encoded logging events to the console. |
| `src/main/java/com/example/logging/MyAppenderWithEncoder.java` | Custom appender that delegates event formatting to `MyEncoder`. |
| `src/main/java/com/example/logging/MyEncoder.java` | Formats logging events and supplies encoder header and footer bytes. |
| `src/main/java/com/example/logging/SensitiveDataFilter.java` | Denies messages containing `password`, `token`, or `secret`. |
| `src/main/java/com/example/logging/MyEncoderWithMasking.java` | Encoder experiment that masks password and token values in formatted messages. |
| `src/main/resources/logback.xml` | Default configuration for the custom console appender and sensitive-data filter. |
| `src/main/resources/logback-rolling.xml` | Alternative configuration for rolling plain-text log files. |
| `pom.xml` | Defines the Java 11 Maven project and its SLF4J and Logback dependencies. |

## Default console configuration

The default `logback.xml` attaches `MyAppenderWithEncoder` to the INFO-level root logger and installs `SensitiveDataFilter`. The filter denies messages containing `password`, `token`, or `secret`; accepted events are formatted by `MyEncoder` and written to the console.

## Experiment record

### 1. Inspect logging events with a custom appender

`MyAppender` extends Logback's `AppenderBase<ILoggingEvent>` and prints event details such as logger name, level, formatted message, and thread. It demonstrates that Logback delivers structured event data to an appender after the SLF4J call. It is retained as a simple appender example; the default XML currently uses `MyAppenderWithEncoder` instead.

### 2. Separate event formatting with an encoder

`MyAppenderWithEncoder` delegates formatting to `MyEncoder`, which extends `EncoderBase<ILoggingEvent>`. The encoder formats the level, logger, and message and provides header and footer bytes. The appender writes the header when it starts and footer when it stops. `Application` stops the `LoggerContext` at the end so the appender can write its footer.

### 3. Deny sensitive messages with a filter

`SensitiveDataFilter` returns `DENY` when the formatted message contains `password`, `token`, or `secret` (case-insensitive); other events pass through. The default `logback.xml` attaches this filter to the custom console appender. The sample password and token messages in `Application` exercise this behavior. This filter drops the entire matching event rather than changing its contents.

### 4. Mask sensitive values in an encoder

`MyEncoderWithMasking` demonstrates an alternative to dropping an event: it replaces values following `password=` or `password:` and `token=` or `token:` with `****`, ignoring case. It is an implementation example and is not currently selected by either XML configuration. The default config uses `MyEncoder` together with the deny filter; the rolling config uses Logback's pattern encoder.

### 5. Roll log files by size and date

`logback-rolling.xml` uses Logback's `RollingFileAppender` with `SizeAndTimeBasedRollingPolicy`. The active file is `logs/application.log`; at 5 KB it rolls to an uncompressed archive named with the date and an index, for example `application.2026-10-11.0.log`. The index allows multiple rollovers on the same day. With the daily `%d{yyyy-MM-dd}` pattern, `maxHistory` of 3 retains three days of archives and cleans older periods. `Application` emits 500 rollover-test messages to make it easy to reach the size threshold.

The rolling configuration is independent of the default console configuration. It currently has no `SensitiveDataFilter`, so sample sensitive messages are written to the rolling log if that configuration is selected. Avoid logging real credentials in either experiment.

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

`Application` emits 500 rollover test messages so the 5 KB threshold is crossed during a run. Check the `logs` directory for the active file and date-indexed archives.

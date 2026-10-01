error id: file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java:java/lang/String#
file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java
empty definition using pc, found symbol in pc: java/lang/String#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 382
uri: file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java
text:
```scala
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;

public class PlainJavaLoggingDemo {
    public static void main(S@@tring[] args) throws Exception {
        Logger logger = Logger.getLogger("order-service");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);

        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
 
        Path workingDirectory = Path.of("").toAbsolutePath();
        Path logDirectory = workingDirectory.getFileName().toString().equals("level-1-core-java")
            ? workingDirectory
            : workingDirectory.resolve("level-1-core-java");
        Files.createDirectories(logDirectory);
        Path logFile = logDirectory.resolve("order-service.log");

        Handler fileHandler = new FileHandler(logFile.toString(), true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);
        System.out.println("File log: " + logFile);

        logger.fine("Debug details for a new order");
        logger.info("Order received");
        logger.warning("Payment is taking longer than expected");
        logger.severe("Could not save the order");

        logger.removeHandler(handler);
        handler.close();
        logger.removeHandler(fileHandler);
        fileHandler.close();
    }

    private static class SimpleFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            return "[" + record.getLevel() + "] "
                    + record.getLoggerName() + " - "
                    + formatMessage(record) + System.lineSeparator();
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/String#
error id: file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java:java/util/logging/Logger#removeHandler().
file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java
empty definition using pc, found symbol in pc: java/util/logging/Logger#removeHandler().
found definition using semanticdb; symbol local3
empty definition using fallback
non-local guesses:

offset: 1946
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
    public static void main(String[] args) throws Exception {
        Logger orderLogger  = Logger.getLogger("order-service");
        orderLogger.setUseParentHandlers(false);
        orderLogger.setLevel(Level.ALL);

        Logger paymentLogger = Logger.getLogger("payment-service");
        paymentLogger.setUseParentHandlers(false);
        paymentLogger.setLevel(Level.ALL);

        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.WARNING);
        handler.setFormatter(new SimpleFormatter());
        orderLogger.addHandler(handler);
        paymentLogger.addHandler(handler);
 
        Path workingDirectory = Path.of("").toAbsolutePath();
        Path logDirectory = workingDirectory.getFileName().toString().equals("level-1-core-java")
            ? workingDirectory
            : workingDirectory.resolve("level-1-core-java");
        Files.createDirectories(logDirectory);
        Path logFile = logDirectory.resolve("order-service.log");

        Handler fileHandler = new FileHandler(logFile.toString(), true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        orderLogger.addHandler(fileHandler);
        System.out.println("File log: " + logFile);

        orderLogger.fine("Debug details for a new order");
        orderLogger.info("Order received");
        orderLogger.warning("Payment is taking longer than expected");
        orderLogger.severe("Could not save the order");
        paymentLogger.info("Payment initiated");

        paymentLogger.removeHandler@@(handler);
        orderLogger.removeHandler(handler);
        handler.close();
        orderLogger.removeHandler(fileHandler);
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

empty definition using pc, found symbol in pc: java/util/logging/Logger#removeHandler().
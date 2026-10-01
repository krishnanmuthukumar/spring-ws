error id: file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java:java/util/logging/Level#WARNING.
file:///C:/Krishnan/Java/spring-ws/observablity/level-1-core-java/PlainJavaLoggingDemo.java
empty definition using pc, found symbol in pc: java/util/logging/Level#WARNING.
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 584
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

public class PlainJavaLoggingDemo {
    public static void main(String[] args) throws Exception {
        Logger logger = Logger.getLogger("order-service");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);

        Handler handler = new ConsoleHandler();
        handler.setLevel(Level.@@WARNING);
        handler.setFormatter(new SimpleFormatter());
        logger.addHandler(handler);
 
        Handler fileHandler = new FileHandler("order-service.log", true);
        fileHandler.setLevel(Level.WARNING);
        fileHandler.setFormatter(new SimpleFormatter());
        logger.addHandler(fileHandler);

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

empty definition using pc, found symbol in pc: java/util/logging/Level#WARNING.
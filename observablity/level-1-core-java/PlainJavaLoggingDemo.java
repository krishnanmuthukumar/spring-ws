import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class PlainJavaLoggingDemo { 
    public static void main(String[] args) throws Exception { 
        Logger orderLogger = Logger.getLogger("order-service"); // Gets the named logger used for order events.
        orderLogger.setUseParentHandlers(false); // Prevents the default parent logger from duplicating output.
        orderLogger.setLevel(Level.ALL); // Allows every severity level to reach this logger's handlers.

        Logger paymentLogger = Logger.getLogger("payment-service"); // Gets a separate named logger for payment events.
        paymentLogger.setUseParentHandlers(false); // Prevents duplicate output through parent handlers.
        paymentLogger.setLevel(Level.ALL); // Allows every severity level for payment events.

        Handler handler = new ConsoleHandler(); // Creates a handler that publishes records in the console.
        handler.setLevel(Level.ALL); // Lets the console handler publish every level passed by a logger.
        handler.setFormatter(new SimpleFormatter()); // Uses the custom formatter for console output.
        orderLogger.addHandler(handler); // Routes order events to the console handler.
        paymentLogger.addHandler(handler); // Routes payment events to the same console handler.

        Path workingDirectory = Path.of("").toAbsolutePath(); // Gets the directory from which Java was launched.
        Path logDirectory = workingDirectory.getFileName().toString().equals("level-1-core-java") // Checks whether Java started inside the level folder.
                ? workingDirectory // Uses the current directory when it is already level-1-core-java.
                : workingDirectory.resolve("level-1-core-java"); // Otherwise, selects that folder under the current directory.
        Files.createDirectories(logDirectory); // Creates the selected log folder if it is missing.
        Path logFile = logDirectory.resolve("order-service.log"); // Builds the path for the output log file.

        Handler fileHandler = new FileHandler(logFile.toString(), true); // Opens the log file and appends instead of replacing it.
        fileHandler.setLevel(Level.ALL); // Lets the file handler publish every level passed by a logger.
        fileHandler.setFormatter(new SimpleFormatter()); // Uses the same custom format for file output.
        orderLogger.addHandler(fileHandler); // Routes order events to the file handler.
        paymentLogger.addHandler(fileHandler); // Routes payment events to the same file handler.
        System.out.println("File log: " + logFile); // Prints the destination so it is easy to find.

        orderLogger.fine("Debug details for a new order"); // Creates a detailed diagnostic event.
        orderLogger.info("Order received"); // Creates a normal informational event.
        orderLogger.warning("Payment is taking longer than expected"); // Creates a warning about a possible issue.
        orderLogger.severe("Could not save the order"); // Creates a severe event for a failure.

        paymentLogger.info("Payment initiated"); // Creates an informational event from the payment logger.

        paymentLogger.removeHandler(handler); // Detaches the console handler from the payment logger.
        orderLogger.removeHandler(handler); // Detaches the console handler from the order logger.
        handler.close(); // Releases the console handler's resources.
        orderLogger.removeHandler(fileHandler); // Detaches the file handler from the order logger.
        fileHandler.close(); // Flushes remaining records and releases the file.
    } 

    private static class SimpleFormatter extends Formatter { // Defines the custom formatter used by both handlers.
        @Override // Marks this as the Formatter method implementation.
        public String format(LogRecord record) { // Converts one logging event into a string.
            return "[" + record.getLevel() + "] " // Adds the severity level at the start of the line.
                    + record.getLoggerName() + " - " // Adds the logger name to identify the source.
                    + formatMessage(record) + System.lineSeparator(); // Adds the message and a platform-specific newline.
        } 
    } 
} 

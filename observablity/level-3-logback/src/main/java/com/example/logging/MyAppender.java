package com.example.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class MyAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent event) {

        System.out.println("========== CUSTOM APPENDER ==========");

//        System.out.println("Logger  : " + event.getLoggerName());
//        System.out.println("Level   : " + event.getLevel());
//        System.out.println("Message : " + event.getFormattedMessage());
//        System.out.println("Thread  : " + event.getThreadName());
        
        System.out.println("Logger       : " + event.getLoggerName());
        System.out.println("Level        : " + event.getLevel());
        System.out.println("Message      : " + event.getMessage());
        System.out.println("Formatted    : " + event.getFormattedMessage());
        System.out.println("Thread       : " + event.getThreadName());
        System.out.println("Timestamp    : " + event.getTimeStamp());

        if (event.getThrowableProxy() != null) {
            System.out.println("Exception    : "
                    + event.getThrowableProxy().getClassName());
        }

        System.out.println("=====================================");
    }
}
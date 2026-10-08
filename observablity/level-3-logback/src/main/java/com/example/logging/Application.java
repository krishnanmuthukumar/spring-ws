package com.example.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {

		log.info("Application started");

		log.info("Order {} created", "ORD-123");

		log.warn("This is a warning");

		log.error("Something went wrong");

		log.info("User password is password123");

		log.info("Authentication token is abc123");

		try {
			throw new RuntimeException("Database unavailable");
		} catch (Exception e) {
			log.error("Order processing failed", e);
		}

		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		context.stop();

	}
}
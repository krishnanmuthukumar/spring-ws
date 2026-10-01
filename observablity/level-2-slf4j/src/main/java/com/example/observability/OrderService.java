package com.example.observability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public static void main(String[] args) {
        logger.debug("Preparing order {}", "ORD-1001");
        logger.info("Order {} received", "ORD-1001");
        logger.warn("Payment for order {} is taking longer than expected", "ORD-1001");
        logger.error("Could not save order {}", "ORD-1001");
    }
}
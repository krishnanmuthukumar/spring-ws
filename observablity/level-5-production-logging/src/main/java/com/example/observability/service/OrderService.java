package com.example.observability.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.observability.exception.OrderProcessingException;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public String createOrder(String orderId) {
        logger.info("Processing order {}", orderId);
        logger.debug("Validating order {}", orderId);

        if (orderId.startsWith("FAIL-")) {
            try {
                simulateDependencyFailure(orderId);
            } catch (IllegalStateException exception) {
                logger.error("Payment failed for order {}", orderId, exception);
                throw new OrderProcessingException("Payment dependency failed for order " + orderId, exception);
            }
        }

        logger.info("Order {} created successfully", orderId);
        return "Order " + orderId + " created";
    }

    private void simulateDependencyFailure(String orderId) {
        logger.warn("Payment dependency is unavailable for order {}", orderId);
        throw new IllegalStateException("Simulated payment dependency failure");
    }
}

package com.example.observability.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    public String createOrder(String orderId) {
        logger.info("Processing order {}", orderId);
        logger.debug("Validating order {}", orderId);
        return "Order " + orderId + " created";
    }
}

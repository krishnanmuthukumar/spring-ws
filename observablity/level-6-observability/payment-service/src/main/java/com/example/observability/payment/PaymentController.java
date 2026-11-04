package com.example.observability.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse chargePayment(
            @RequestParam String orderId) {
        logger.info("Charging payment for order {}", orderId);
        return new PaymentResponse(orderId, "PAID");
    }

    public record PaymentResponse(String orderId, String status) {
    }
}

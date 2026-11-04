package com.example.observability.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderWorkflowService orderWorkflowService;

    public OrderController(OrderWorkflowService orderWorkflowService) {
        this.orderWorkflowService = orderWorkflowService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderWorkflowService.OrderResult createOrder(
            @RequestParam(defaultValue = "ORD-1001") String orderId) {
        return orderWorkflowService.createOrder(orderId);
    }
}

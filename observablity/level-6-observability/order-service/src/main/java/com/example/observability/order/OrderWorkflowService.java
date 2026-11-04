package com.example.observability.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static com.example.observability.order.CorrelationIdFilter.HEADER;
import static com.example.observability.order.CorrelationIdFilter.MDC_KEY;

@Service
public class OrderWorkflowService {

    private static final Logger logger = LoggerFactory.getLogger(OrderWorkflowService.class);

    private final RestClient restClient;
    private final String paymentServiceUrl;
    private final String notificationServiceUrl;

    public OrderWorkflowService(
            RestClient.Builder restClientBuilder,
            @Value("${services.payment.url}") String paymentServiceUrl,
            @Value("${services.notification.url}") String notificationServiceUrl) {
        this.restClient = restClientBuilder.build();
        this.paymentServiceUrl = paymentServiceUrl;
        this.notificationServiceUrl = notificationServiceUrl;
    }

    public OrderResult createOrder(String orderId) {
        logger.info("Creating order {}", orderId);

        ServiceResponse payment = restClient.post()
                .uri(paymentServiceUrl + "/payments?orderId={orderId}", orderId)
                .header(HEADER, MDC.get(MDC_KEY))
                .retrieve()
                .body(ServiceResponse.class);
        logger.info("Payment completed for order {} with status {}", orderId, payment.status());

        ServiceResponse notification = restClient.post()
                .uri(notificationServiceUrl + "/notifications?orderId={orderId}", orderId)
                .header(HEADER, MDC.get(MDC_KEY))
                .retrieve()
                .body(ServiceResponse.class);
        logger.info("Notification queued for order {} with status {}", orderId, notification.status());

        return new OrderResult(orderId, "CREATED", payment.status(), notification.status());
    }

    public record OrderResult(
            String orderId,
            String status,
            String paymentStatus,
            String notificationStatus) {
    }

    private record ServiceResponse(String orderId, String status) {
    }
}

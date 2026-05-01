package com.vivaeventos.orderservice.kafka;

import com.vivaeventos.orderservice.domain.Order;
import com.vivaeventos.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    private final OrderService orderService;

    public PaymentConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    // Pago exitoso → confirmar orden → dispara ticket y notificación
    @KafkaListener(topics = "${kafka.topics.payment-confirmed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentConfirmed(Order order) {
        log.info("Pago confirmado para orden {}", order.getId());
        orderService.confirmOrder(order.getId());
    }

    // Pago fallido → cancelar orden → libera el aforo
    @KafkaListener(topics = "${kafka.topics.payment-failed}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentFailed(Order order) {
        log.warn("Pago fallido para orden {}", order.getId());
        orderService.cancelOrder(order.getId(), "PAYMENT_FAILED");
    }

    // Pago pendiente → marcar en espera (no liberar aforo todavía)
    @KafkaListener(topics = "${kafka.topics.payment-pending}", groupId = "${spring.kafka.consumer.group-id}")
    public void handlePaymentPending(Order order) {
        log.info("Pago pendiente para orden {}", order.getId());
        orderService.markPaymentPending(order.getId());
    }
}

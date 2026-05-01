package com.vivaeventos.orderservice.kafka;

import com.vivaeventos.orderservice.domain.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${kafka.topics.order-confirmed}")
    private String orderConfirmedTopic;

    @Value("${kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(Order order) {
        kafkaTemplate.send(orderCreatedTopic, order.getId().toString(), order);
    }

    public void publishOrderConfirmed(Order order) {
        kafkaTemplate.send(orderConfirmedTopic, order.getId().toString(), order);
    }

    // faltaba este método — lo necesita US-13 (cancelación) y payment.failed
    public void publishOrderCancelled(Order order) {
        kafkaTemplate.send(orderCancelledTopic, order.getId().toString(), order);
    }
}
package com.vivaeventos.orderservice.service;

import com.vivaeventos.orderservice.domain.Order;
import com.vivaeventos.orderservice.dto.CreateOrderRequest;
import com.vivaeventos.orderservice.dto.OrderResponse;
import com.vivaeventos.orderservice.exception.OrderNotFoundException;
import com.vivaeventos.orderservice.kafka.OrderEventPublisher;
import com.vivaeventos.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final OrderEventPublisher publisher;

    public OrderService(OrderRepository repository, OrderEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // Idempotencia: si ya existe una orden con esa key, devuelve la existente
        if (request.idempotencyKey() != null) {
            return repository.findByIdempotencyKey(request.idempotencyKey())
                    .map(OrderResponse::from)
                    .orElseGet(() -> OrderResponse.from(saveNewOrder(request)));
        }

        return OrderResponse.from(saveNewOrder(request));
    }

    private Order saveNewOrder(CreateOrderRequest request) {

        // Precio base fijo por tipo de boleta (en un MVP real vendría del event-service)
        // Cuando event-service esté listo, aquí se hace la llamada o se lee del evento Kafka
        BigDecimal unitPrice = resolvePrecio(request.ticketType());

        // Descuento: por ahora 0 si no hay promo. La lógica de PromoCode se amplía en US-07
        BigDecimal discountPct = BigDecimal.ZERO;

        // Total calculado siempre en el servidor — nunca confiar en el cliente
        BigDecimal total = unitPrice
                .multiply(BigDecimal.valueOf(request.quantity()))
                .multiply(BigDecimal.ONE.subtract(discountPct.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)))
                .setScale(2, RoundingMode.HALF_UP);

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .eventId(request.eventId())
                .customerId(request.customerId())
                .ticketType(request.ticketType())
                .quantity(request.quantity())
                .unitPrice(unitPrice)
                .discountPct(discountPct)
                .totalAmount(total)
                .status("PENDING")
                .idempotencyKey(request.idempotencyKey())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Order saved = repository.save(order);

        // Inicia el proceso de pago vía Kafka (criterio 2 de US-04)
        publisher.publishOrderCreated(saved);

        return saved;
    }

    public OrderResponse getOrderById(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        return OrderResponse.from(order);
    }

    @Transactional
    public void confirmOrder(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus("CONFIRMED");
        order.setUpdatedAt(LocalDateTime.now());
        repository.save(order);

        publisher.publishOrderConfirmed(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId, String motivo) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus("CANCELLED");
        order.setUpdatedAt(LocalDateTime.now());
        repository.save(order);

        publisher.publishOrderCancelled(order);
    }

    @Transactional
    public void markPaymentPending(UUID orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus("PAYMENT_PROCESSING");
        order.setUpdatedAt(LocalDateTime.now());
        repository.save(order);
    }

    // Resuelve precio por tipo. En MVP real esto vendría del event-service via Kafka o HTTP
    private BigDecimal resolvePrecio(String ticketType) {
        return switch (ticketType.toUpperCase()) {
            case "VIP"      -> new BigDecimal("250000");
            case "STUDENT"  -> new BigDecimal("50000");
            default         -> new BigDecimal("120000"); // GENERAL
        };
    }
}
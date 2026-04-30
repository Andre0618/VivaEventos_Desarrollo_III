package com.vivaeventos.orderservice.controller;

import com.vivaeventos.orderservice.dto.CreateOrderRequest;
import com.vivaeventos.orderservice.dto.OrderResponse;
import com.vivaeventos.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // US-04 criterio 1: crear orden — recibe DTO, nunca la entidad directa
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = service.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // US-04 testabilidad: consultar estado de una orden
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id) {
        OrderResponse response = service.getOrderById(id);
        return ResponseEntity.ok(response);
    }
}
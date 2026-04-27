package com.vivaeventos.orderservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID eventId;

    private UUID customerId;

    private String ticketType;

    private Integer quantity;

    private Double unitPrice;

    private Double discountPct;

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "promo_code_id")
    private PromoCode promoCode;

    private String status;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
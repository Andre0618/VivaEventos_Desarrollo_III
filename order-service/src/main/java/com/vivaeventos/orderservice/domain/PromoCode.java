package com.vivaeventos.orderservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promo_codes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true)
    private String code;

    private Double discountPct;

    private Integer maxUses;

    private Integer usedCount;

    private LocalDateTime validFrom;

    private LocalDateTime validUntil;

    private UUID eventId;

    private Boolean active;

    private LocalDateTime createdAt;
}
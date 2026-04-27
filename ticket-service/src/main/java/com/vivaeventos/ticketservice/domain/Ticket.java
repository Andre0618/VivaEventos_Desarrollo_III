package com.vivaeventos.ticketservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID orderId;

    private UUID eventId;

    private UUID customerId;

    private String ticketType;

    @Column(unique = true)
    private String uniqueCode;

    private String qrImageUrl;

    private String status;

    private LocalDateTime validatedAt;

    private LocalDateTime generatedAt;
}
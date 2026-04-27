package com.vivaeventos.eventservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String description;

    private String category;

    private String venue;

    private LocalDateTime eventDate;

    private Integer capacity;

    private Integer availableTickets;

    private Double price;

    private String status;

    private UUID organizerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
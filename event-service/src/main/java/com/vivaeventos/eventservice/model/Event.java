package com.vivaeventos.eventservice.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @Entity  → Le dice a Spring que esta clase representa una tabla en la BD
 * @Table   → Le dice qué tabla es (debe coincidir exactamente con el SQL de Flyway)
 */
@Entity
@Table(name = "events")
public class Event {

    /**
     * @Id         → Esta es la llave primaria
     * @GeneratedValue → La BD genera el UUID automáticamente (gen_random_uuid() en el SQL)
     * @Column     → Mapea el campo al nombre exacto de la columna en la tabla
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "venue", nullable = false, length = 255)
    private String venue;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    // BigDecimal es el tipo correcto para precios (evita errores de redondeo)
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // El status viene de un enum: ACTIVE, CANCELLED, SOLD_OUT
    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    // ID del organizador — viene del token JWT en los otros servicios
    @Column(name = "organizer_id", nullable = false)
    private UUID organizerId;

    // @Column(insertable=false) → la BD pone el valor automáticamente con DEFAULT now()
    @Column(name = "created_at", nullable = false, updatable = false)
    @org.hibernate.annotations.CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @org.hibernate.annotations.UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Enum de estados válidos (igual que en el SQL) ──────────────────
    public enum EventStatus {
        ACTIVE,
        CANCELLED,
        SOLD_OUT
    }

    // ── Constructor vacío obligatorio para JPA ─────────────────────────
    public Event() {}

    // ── Getters y Setters ──────────────────────────────────────────────
    // (En producción usarías Lombok @Getter @Setter para no escribir esto,
    //  pero aquí lo dejamos explícito para que veas qué hace cada uno)

    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }

    public UUID getOrganizerId() { return organizerId; }
    public void setOrganizerId(UUID organizerId) { this.organizerId = organizerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
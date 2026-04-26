package com.vivaeventos.eventservice.dto;

import com.vivaeventos.eventservice.model.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta — lo que devuelves al cliente en el JSON de respuesta.
 *
 * ¿Por qué no devolver la entidad Event directamente?
 * Porque la entidad pertenece a la capa de base de datos y puede tener
 * campos internos que no quieres exponer. El DTO te da control total
 * sobre qué información sale hacia afuera.
 *
 * Ejemplo de JSON que devuelves:
 * {
 *   "id": "a3f8c2d1-...",
 *   "name": "Concierto Rock en el Parque",
 *   "category": "CONCIERTO",
 *   "venue": "Parque Simón Bolívar, Bogotá",
 *   "eventDate": "2025-08-15T18:00:00",
 *   "capacity": 5000,
 *   "price": 85000.00,
 *   "status": "ACTIVE",
 *   "organizerId": "550e8400-...",
 *   "createdAt": "2025-04-25T10:30:00"
 * }
 */
public class EventResponse {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private String venue;
    private LocalDateTime eventDate;
    private Integer capacity;
    private BigDecimal price;
    private String status;
    private UUID organizerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor vacío
    public EventResponse() {}

    /**
     * Método estático de fábrica: convierte una entidad Event en un EventResponse.
     * Se usa en el Service así: EventResponse.from(event)
     *
     * Esto mantiene la lógica de conversión en el DTO y no ensucia el Service.
     */
    public static EventResponse from(Event event) {
        EventResponse response = new EventResponse();
        response.id          = event.getId();
        response.name        = event.getName();
        response.description = event.getDescription();
        response.category    = event.getCategory();
        response.venue       = event.getVenue();
        response.eventDate   = event.getEventDate();
        response.capacity    = event.getCapacity();
        response.price       = event.getPrice();
        response.status      = event.getStatus().name();  // Enum → String
        response.organizerId = event.getOrganizerId();
        response.createdAt   = event.getCreatedAt();
        response.updatedAt   = event.getUpdatedAt();
        return response;
    }

    // ── Getters (sin setters — la respuesta es inmutable desde afuera) ──
    public UUID getId()                  { return id; }
    public String getName()              { return name; }
    public String getDescription()       { return description; }
    public String getCategory()          { return category; }
    public String getVenue()             { return venue; }
    public LocalDateTime getEventDate()  { return eventDate; }
    public Integer getCapacity()         { return capacity; }
    public BigDecimal getPrice()         { return price; }
    public String getStatus()            { return status; }
    public UUID getOrganizerId()         { return organizerId; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getUpdatedAt()  { return updatedAt; }
}
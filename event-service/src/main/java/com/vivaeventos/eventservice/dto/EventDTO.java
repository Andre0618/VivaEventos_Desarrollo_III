package com.vivaeventos.eventservice.dto;

import com.vivaeventos.eventservice.model.Event;
import java.time.LocalDateTime;

/**
 * DTO simplificado para el catálogo de eventos
 *
 * Solo expone los campos necesarios:
 *   - Nombre del evento
 *   - Fecha del evento
 *   - Lugar del evento
 */
public class EventDTO {

    private String name;
    private LocalDateTime eventDate;
    private String venue;

    // Constructor vacío
    public EventDTO() {}

    /**
     * Constructor con parámetros
     */
    public EventDTO(String name, LocalDateTime eventDate, String venue) {
        this.name = name;
        this.eventDate = eventDate;
        this.venue = venue;
    }

    /**
     * Método estático de fábrica: convierte una entidad Event en un EventDTO.
     *
     * Se usa así: EventDTO.from(event)
     */
    public static EventDTO from(Event event) {
        return new EventDTO(
                event.getName(),
                event.getEventDate(),
                event.getVenue()
        );
    }

    // ── Getters ──
    public String getName() {
        return name;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public String getVenue() {
        return venue;
    }

    // ── Setters (aunque típicamente no se usan en DTOs de respuesta) ──
    public void setName(String name) {
        this.name = name;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }
}


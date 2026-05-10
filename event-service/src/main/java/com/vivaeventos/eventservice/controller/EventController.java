package com.vivaeventos.eventservice.controller;

import com.vivaeventos.eventservice.dto.CreateEventRequest;
import com.vivaeventos.eventservice.dto.EventFilterRequest;
import com.vivaeventos.eventservice.dto.EventResponse;
import com.vivaeventos.eventservice.dto.EventDTO;
import com.vivaeventos.eventservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.vivaeventos.eventservice.dto.UpdatePriceRequest;
import com.vivaeventos.eventservice.exception.EventNotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * POST /events — Crea un nuevo evento (US-01)
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /events/catalog — Obtiene todos los eventos disponibles (US-02)
     *
     * Criterios de aceptación:
     * - Mostrar nombre, fecha y lugar
     * - Permitir ver todos los eventos disponibles
     *
     * Respuesta:
     * - Si hay eventos → devuelve lista con HTTP 200
     * - Si no hay eventos → devuelve mensaje con HTTP 200
     */
    @GetMapping("/catalog")
    public ResponseEntity<?> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();

        if (events.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "No hay eventos disponibles en el catálogo")
            );
        }

        return ResponseEntity.ok(events);
    }

    /**
     * GET /events?category=Rock&dateFrom=2025-08-01T00:00:00&dateTo=2025-08-31T23:59:59
     *
     * Filtra eventos por categoría y/o rango de fechas (US-03).
     * Todos los parámetros son opcionales.
     *
     * Criterio 1: hay resultados → devuelve lista con HTTP 200
     * Criterio 2: no hay resultados → devuelve mensaje adecuado con HTTP 200
     */
    @GetMapping
    public ResponseEntity<?> filterEvents(@ModelAttribute EventFilterRequest filter) {

        List<EventResponse> events = eventService.filterEvents(
                filter.getCategory(),
                filter.getDateFrom(),
                filter.getDateTo()
        );

        if (events.isEmpty()) {
            return ResponseEntity.ok(
                    Map.of("message", "No se encontraron eventos para los filtros aplicados")
            );
        }

        return ResponseEntity.ok(events);
    }

    /**
     * PATCH /events/{id}/price
     *
     * Modifica el precio de las boletas de un evento (US-11).
     *
     * ¿Por qué PATCH y no PUT?
     * - PUT reemplaza el recurso completo (requeriría enviar todos los campos del evento)
     * - PATCH modifica solo un campo específico → más eficiente y semánticamente correcto
     *
     * @PathVariable → Lee el {id} de la URL y lo convierte a UUID automáticamente.
     *                 Ejemplo: PATCH /events/550e8400-e29b-41d4-a716-446655440000/price
     *
     * @Valid → Activa las validaciones de UpdatePriceRequest (@NotNull, @DecimalMin).
     *          Si el precio es negativo o falta, Spring devuelve HTTP 400 automáticamente.
     *
     * Respuestas posibles:
     *  - HTTP 200 OK          → precio actualizado exitosamente
     *  - HTTP 400 Bad Request → precio inválido (negativo o faltante)
     *  - HTTP 404 Not Found   → evento no existe
     *  - HTTP 409 Conflict    → evento ya inició o no está activo
     *
     * Ejemplo de llamada:
     * PATCH http://localhost:8081/events/550e8400-e29b-41d4-a716-446655440000/price
     * Content-Type: application/json
     * { "newPrice": 95000.00 }
     */
    @PatchMapping("/{id}/price")
    public ResponseEntity<?> updatePrice(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePriceRequest request) {
        try {
            EventResponse response = eventService.updatePrice(id, request);
            return ResponseEntity.ok(response);

        } catch (EventNotFoundException e) {
            // HTTP 404 → el evento no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));

        } catch (IllegalStateException e) {
            // HTTP 409 Conflict → el evento ya inició o no está activo
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}

/**Anotación por si hay error de tipado o consistencia:
 * ser cuidadosos con ResponseEntity<?>, puesto que podría generar
 * inconsistencias al no conocer con exactitud lo que se recibe. Se deja sin correcciones
 * por funcionamiento correcto
 */
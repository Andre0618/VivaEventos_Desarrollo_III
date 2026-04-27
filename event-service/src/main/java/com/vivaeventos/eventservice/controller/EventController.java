package com.vivaeventos.eventservice.controller;

import com.vivaeventos.eventservice.dto.CreateEventRequest;
import com.vivaeventos.eventservice.dto.EventResponse;
import com.vivaeventos.eventservice.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @RestController → Esta clase maneja peticiones HTTP y devuelve JSON automáticamente.
 *                   Es la combinación de @Controller + @ResponseBody.
 *
 * @RequestMapping → Todos los endpoints de esta clase empiezan con /events
 *
 * RESPONSABILIDADES de esta capa:
 *  - Recibir la petición HTTP
 *  - Llamar al Service con los datos del request
 *  - Devolver la respuesta HTTP correcta (código de estado + body JSON)
 *
 * Lo que NO hace el controller: lógica de negocio ni acceso a BD.
 * Si el controller tiene más de 5 líneas por método, algo está mal.
 */
@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Inyección por constructor
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * POST /events
     *
     * Crea un nuevo evento.
     *
     * @PostMapping  → Este método responde al método HTTP POST en la ruta /events
     * @RequestBody  → Lee el JSON del body de la petición y lo convierte en CreateEventRequest
     * @Valid        → Activa las validaciones definidas en CreateEventRequest
     *                 (si algo falla, Spring devuelve 400 Bad Request automáticamente)
     *
     * ResponseEntity<EventResponse> → Te permite controlar el código HTTP de respuesta.
     * HTTP 201 Created es el código correcto cuando creas un recurso (no 200 OK).
     *
     * Ejemplo de llamada:
     * POST http://localhost:8081/events
     * Content-Type: application/json
     * {
     *   "name": "Concierto Rock",
     *   "venue": "Parque Simón Bolívar",
     *   "eventDate": "2025-08-15T18:00:00",
     *   "capacity": 5000,
     *   "price": 85000.00,
     *   "organizerId": "550e8400-e29b-41d4-a716-446655440000"
     * }
     */
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        EventResponse response = eventService.createEvent(request);
        // 201 Created + el evento creado en el body
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
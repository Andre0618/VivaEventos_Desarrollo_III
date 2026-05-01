package com.vivaeventos.eventservice.service;

import com.vivaeventos.eventservice.dto.CreateEventRequest;
import com.vivaeventos.eventservice.dto.EventResponse;
import com.vivaeventos.eventservice.model.Event;
import com.vivaeventos.eventservice.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Service → Marca esta clase como componente de lógica de negocio.
 *
 * RESPONSABILIDADES de esta capa:
 *  1. Recibir el DTO del controller
 *  2. Convertirlo a entidad (Event)
 *  3. Guardarlo en la BD con el repository
 *  4. Publicar el evento en Kafka (para que otros microservicios se enteren)
 *  5. Devolver el EventResponse al controller
 *
 * Lo que NO hace el service: saber nada de HTTP (sin HttpRequest, sin ResponseEntity).
 * Eso es responsabilidad exclusiva del Controller.
 */
@Service
public class EventService {

    // Logger para registrar qué pasa (útil para depurar y para auditoría)
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    // Spring inyecta estas dependencias automáticamente (@Autowired implícito en constructor)
    private final EventRepository eventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Leemos el nombre del topic desde application.yml
    // Si no existe la propiedad, usa "event.created" como valor por defecto
    @Value("${kafka.topics.event-created:event.created}")
    private String eventCreatedTopic;

    // Inyección por constructor (recomendada sobre @Autowired en campo)
    public EventService(EventRepository eventRepository,
                        KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.kafkaTemplate   = kafkaTemplate;
    }

    /**
     * Crea un nuevo evento.
     *
     * @Transactional garantiza que si algo falla a mitad del proceso,
     * la BD hace rollback automático (no quedan datos a medias).
     *
     * @param request  DTO con los datos del evento (ya validados por el controller)
     * @return         EventResponse con los datos del evento creado (incluye el UUID generado)
     */
    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        log.info("Creando evento: '{}' para organizador {}", request.getName(), request.getOrganizerId());

        // 1. Convertir el DTO en entidad
        Event event = new Event();
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setEventDate(request.getEventDate());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setOrganizerId(request.getOrganizerId());
        event.setStatus(Event.EventStatus.ACTIVE);  // Todo evento nuevo nace como ACTIVE

        // 2. Guardar en la BD — JPA genera el INSERT automáticamente
        //    savedEvent tiene el UUID que generó la BD
        Event savedEvent = eventRepository.save(event);
        log.info("Evento guardado con ID: {}", savedEvent.getId());

        // 3. Publicar mensaje en Kafka para notificar a otros microservicios
        //    (por ejemplo: notification-service puede enviar confirmación al organizador)
        publishEventCreated(savedEvent);

        // 4. Convertir la entidad guardada en DTO de respuesta y devolverlo
        return EventResponse.from(savedEvent);
    }
    /**
     * Filtra eventos por categoría y/o rango de fechas.
     * Si no hay resultados, devuelve lista vacía (el controller maneja el mensaje).
     */
    public List<EventResponse> filterEvents(String category, LocalDateTime dateFrom, LocalDateTime dateTo) {
        log.info("Filtrando eventos - categoría: {}, desde: {}, hasta: {}", category, dateFrom, dateTo);

        List<Event> events = eventRepository.findByFilters(
                Event.EventStatus.ACTIVE,
                category,
                dateFrom,
                dateTo
        );

        log.info("Se encontraron {} eventos con los filtros aplicados", events.size());

        return events.stream()
                .map(EventResponse::from)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Publica un mensaje en el topic de Kafka "event.created".
     * Los microservicios suscritos (notification-service, etc.) lo recibirán.
     *
     * Si Kafka no está disponible, logueamos el error pero NO lanzamos excepción
     * para no romper el flujo de creación del evento (el evento ya se guardó en BD).
     */
    private void publishEventCreated(Event event) {
        try {
            // El mensaje que enviamos a Kafka es el EventResponse (serializado a JSON)
            EventResponse message = EventResponse.from(event);

            // send(topic, key, value)
            // key = ID del evento → permite particionar mensajes del mismo evento juntos
            kafkaTemplate.send(eventCreatedTopic, event.getId().toString(), message);

            log.info("Mensaje publicado en Kafka topic '{}' para evento {}", eventCreatedTopic, event.getId());
        } catch (Exception e) {
            // En producción usarías un outbox pattern o retry, pero para el MVP esto es suficiente
            log.error("Error al publicar en Kafka el evento {}: {}", event.getId(), e.getMessage());
        }

    }
}
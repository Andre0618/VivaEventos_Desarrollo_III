package com.vivaeventos.eventservice;

import com.vivaeventos.eventservice.dto.EventResponse;
import com.vivaeventos.eventservice.model.Event;
import com.vivaeventos.eventservice.repository.EventRepository;
import com.vivaeventos.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceFilterTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventService eventService;

    private Event eventoActivo;

    @BeforeEach
    void setUp() {
        eventoActivo = new Event();
        eventoActivo.setName("Concierto Rock");
        eventoActivo.setCategory("Música");
        eventoActivo.setVenue("Parque Simón Bolívar");
        eventoActivo.setEventDate(LocalDateTime.of(2025, 8, 15, 18, 0));
        eventoActivo.setCapacity(5000);
        eventoActivo.setPrice(BigDecimal.valueOf(85000));
        eventoActivo.setOrganizerId(UUID.randomUUID());
        eventoActivo.setStatus(Event.EventStatus.ACTIVE);
    }

    /**
     * Criterio 1: existen eventos → el sistema devuelve los eventos correspondientes
     */
    @Test
    void dadoQueExistenEventos_cuandoSeFiltraPorCategoria_entoncesDevuelveLista() {
        when(eventRepository.findByFilters(
                eq(Event.EventStatus.ACTIVE),
                eq("Música"),
                any(),
                any()
        )).thenReturn(List.of(eventoActivo));

        List<EventResponse> resultado = eventService.filterEvents("Música", null, null);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getName()).isEqualTo("Concierto Rock");
        assertThat(resultado.get(0).getCategory()).isEqualTo("Música");
    }

    /**
     * Criterio 1: filtrar por rango de fechas devuelve eventos correctos
     */
    @Test
    void dadoQueExistenEventos_cuandoSeFiltraPorFecha_entoncesDevuelveLista() {
        LocalDateTime desde = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2025, 8, 31, 23, 59);

        when(eventRepository.findByFilters(
                eq(Event.EventStatus.ACTIVE),
                any(),
                eq(desde),
                eq(hasta)
        )).thenReturn(List.of(eventoActivo));

        List<EventResponse> resultado = eventService.filterEvents(null, desde, hasta);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getName()).isEqualTo("Concierto Rock");
    }

    /**
     * Criterio 2: no existen eventos para el filtro → devuelve lista vacía
     */
    @Test
    void dadoQueNoExistenEventos_cuandoSeAplicaFiltro_entoncesDevuelveListaVacia() {
        when(eventRepository.findByFilters(
                eq(Event.EventStatus.ACTIVE),
                eq("Teatro"),
                any(),
                any()
        )).thenReturn(Collections.emptyList());

        List<EventResponse> resultado = eventService.filterEvents("Teatro", null, null);

        assertThat(resultado).isEmpty();
    }

    /**
     * Criterio 1: filtrar por categoría Y fecha combina ambos filtros correctamente
     */
    @Test
    void dadoQueExistenEventos_cuandoSeFiltraPorCategoriaYFecha_entoncesDevuelveEventosCorrectos() {
        LocalDateTime desde = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime hasta = LocalDateTime.of(2025, 8, 31, 23, 59);

        when(eventRepository.findByFilters(
                eq(Event.EventStatus.ACTIVE),
                eq("Música"),
                eq(desde),
                eq(hasta)
        )).thenReturn(List.of(eventoActivo));

        List<EventResponse> resultado = eventService.filterEvents("Música", desde, hasta);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCategory()).isEqualTo("Música");
    }
}
package com.vivaeventos.eventservice.repository;

import com.vivaeventos.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    // Filtrar por categoría (solo eventos ACTIVE)
    List<Event> findByCategoryAndStatus(String category, Event.EventStatus status);

    // Filtrar por rango de fechas (solo eventos ACTIVE)
    List<Event> findByEventDateBetweenAndStatus(
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            Event.EventStatus status
    );

    // Filtrar por categoría Y rango de fechas combinados
    @Query("""
            SELECT e FROM Event e
            WHERE e.status = :status
              AND (:category IS NULL OR e.category = :category)
              AND (:dateFrom IS NULL OR e.eventDate >= :dateFrom)
              AND (:dateTo   IS NULL OR e.eventDate <= :dateTo)
            ORDER BY e.eventDate ASC
            """)
    List<Event> findByFilters(
            @Param("status")   Event.EventStatus status,
            @Param("category") String category,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo")   LocalDateTime dateTo
    );
}
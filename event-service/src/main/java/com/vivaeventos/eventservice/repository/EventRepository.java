package com.vivaeventos.eventservice.repository;

import com.vivaeventos.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @Repository → Marca esta interfaz como componente de acceso a datos.
 *
 * JpaRepository<Event, UUID> significa:
 *   - Event  → qué entidad maneja
 *   - UUID   → tipo de la llave primaria
 *
 * Al extender JpaRepository, Spring genera automáticamente el SQL para:
 *   - save(event)       → INSERT INTO events ...
 *   - findById(id)      → SELECT * FROM events WHERE id = ?
 *   - findAll()         → SELECT * FROM events
 *   - deleteById(id)    → DELETE FROM events WHERE id = ?
 *   - count()           → SELECT COUNT(*) FROM events
 *
 * No necesitas escribir ninguna query SQL para estas operaciones básicas.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    // Por ahora no necesitamos métodos adicionales para crear un evento.
    // En las siguientes funcionalidades (listar, filtrar) agregaremos métodos aquí.
}
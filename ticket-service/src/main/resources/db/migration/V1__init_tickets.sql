-- V1__init_tickets.sql
-- Schema inicial del ticket-service
-- Historias: US-06, RQ-05 (validación ≤ 2s), RQ-15 (offline), RQ-06 (no doble uso)

CREATE TABLE tickets (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID           NOT NULL,
    event_id        UUID           NOT NULL,
    customer_id     UUID           NOT NULL,
    ticket_type     VARCHAR(50)    NOT NULL DEFAULT 'GENERAL',
    unique_code     VARCHAR(255)   NOT NULL UNIQUE,  -- Código para el QR
    qr_image_url    VARCHAR(500),                    -- URL del QR almacenado
    status          VARCHAR(50)    NOT NULL DEFAULT 'ACTIVE',
    -- ACTIVE | USED | CANCELLED | EXPIRED
    validated_at    TIMESTAMP,     -- Momento en que se escaneó en puerta
    generated_at    TIMESTAMP      NOT NULL DEFAULT now()
);

-- Registro de intentos de validación (RQ-09, RQ-15)
CREATE TABLE ticket_validations (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id       UUID           NOT NULL REFERENCES tickets(id),
    validated_by    VARCHAR(255),  -- ID del operador de puerta
    result          VARCHAR(50)    NOT NULL,   -- VALID | ALREADY_USED | NOT_FOUND | CANCELLED
    validated_at    TIMESTAMP      NOT NULL DEFAULT now(),
    offline_mode    BOOLEAN        NOT NULL DEFAULT FALSE  -- RQ-15: validación en campo
);

CREATE INDEX idx_tickets_unique_code ON tickets(unique_code);  -- Búsqueda rápida por QR (RQ-05)
CREATE INDEX idx_tickets_event_id    ON tickets(event_id);
CREATE INDEX idx_tickets_status      ON tickets(status);
CREATE INDEX idx_validations_ticket  ON ticket_validations(ticket_id);

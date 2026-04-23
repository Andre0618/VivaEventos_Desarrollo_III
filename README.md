# VivaEventos — Backend MVP

Plataforma de gestión de eventos y venta de boletas digitales.  
**Equipo:** Los Inhackeables1 

## Equipo — Los Inhackeables1

| Integrante                  | Código        |
|-----------------------------|---------------|
| Juan David Ascencio         | 2359660-3743  |
| Geraldine Florez            | 2269476-3743  |
| Diego Gómez                 | 2411026-3743  |
| David Stiven Mujanajinsoy   | 2376834-3743  |
| Andrés Felipe Salcedo       | 2359304-3743  |


---

## Arquitectura general

```
                        ┌─────────────────────────────────────────┐
                        │              API Gateway :8080           │
                        │   Autenticación JWT · Enrutamiento       │
                        └──┬──────┬──────┬──────┬──────┬──────────┘
                           │      │      │      │      │
              ┌────────────▼┐ ┌───▼──┐ ┌▼─────┐│┌─────▼──┐ ┌──────────────┐
              │event-service││order │ │payment│││ticket  │ │notification  │
              │    :8081    ││ :8082│ │ :8083 │││ :8084  │ │   :8085      │
              └──────┬──────┘└──┬───┘ └───┬───┘│└────┬───┘ └──────┬───────┘
                     │          │          │    │     │             │
                     └──────────┴──────────┴────┘─────┴─────────────┘
                                          │
                              ┌───────────▼──────────┐
                              │   Apache Kafka :9092  │
                              │  Bus de eventos async │
                              └──────────────────────┘

Cada servicio tiene su propia base de datos PostgreSQL (R-03).
La comunicación entre servicios es exclusivamente a través de Kafka (R-02, RQ-16).
```

### Servicios y responsabilidades

| Servicio             | Puerto | Responsabilidad principal                           | US / RQ relacionados          |
|----------------------|--------|-----------------------------------------------------|-------------------------------|
| `api-gateway`        | 8080   | Punto de entrada, JWT, enrutamiento                 | US-20, RQ-01, RQ-02           |
| `event-service`      | 8081   | CRUD de eventos, filtrado, cancelación              | US-01–03, US-11, US-12        |
| `order-service`      | 8082   | Órdenes de compra, aforo, códigos promo             | US-04, US-07, US-13, RQ-06    |
| `payment-service`    | 8083   | Integración pasarela sandbox, webhooks              | US-05, US-19, RQ-07, RQ-14    |
| `ticket-service`     | 8084   | Generación QR, validación en puerta                 | US-06, RQ-05, RQ-15           |
| `notification-service`| 8085  | Confirmaciones, recordatorios, reintentos           | US-08, US-09, RQ-04           |

### Topics de Kafka

| Topic                | Productor           | Consumidores                          |
|----------------------|---------------------|---------------------------------------|
| `event.created`      | event-service       | —                                     |
| `event.updated`      | event-service       | —                                     |
| `event.cancelled`    | event-service       | notification-service                  |
| `order.created`      | order-service       | payment-service                       |
| `order.confirmed`    | order-service       | ticket-service, notification-service  |
| `order.cancelled`    | order-service       | notification-service                  |
| `payment.initiated`  | order-service       | payment-service                       |
| `payment.confirmed`  | payment-service     | order-service, ticket-service         |
| `payment.failed`     | payment-service     | order-service, notification-service   |
| `payment.pending`    | payment-service     | order-service                         |
| `ticket.generated`   | ticket-service      | notification-service                  |
| `ticket.validated`   | ticket-service      | —                                     |



---

## Flujo principal de compra

```
Cliente → POST /api/orders        (order-service)
                │
                ├─► Kafka: order.created
                │
        payment-service escucha order.created
                │
                ├─► POST a pasarela sandbox (Wompi)
                ├─► Kafka: payment.pending / payment.failed
                │
        Pasarela llama webhook → POST /api/payments/webhook
                │
                ├─► Kafka: payment.confirmed
                │
        order-service confirma orden → Kafka: order.confirmed
                │
        ticket-service genera QR  → Kafka: ticket.generated
                │
        notification-service envía email de confirmación
```

---

## Endpoints principales (por servicio)

### event-service (`/api/events`)
| Método | Ruta                     | Descripción                  | US     |
|--------|--------------------------|------------------------------|--------|
| POST   | `/`                      | Crear evento                 | US-01  |
| GET    | `/`                      | Listar eventos disponibles   | US-02  |
| GET    | `/?category=&date=`      | Filtrar eventos              | US-03  |
| PATCH  | `/{id}/price`            | Modificar precio             | US-11  |
| DELETE | `/{id}`                  | Cancelar evento              | US-12  |

### order-service (`/api/orders`)
| Método | Ruta                     | Descripción                  | US     |
|--------|--------------------------|------------------------------|--------|
| POST   | `/`                      | Crear orden de compra        | US-04  |
| GET    | `/{id}`                  | Consultar orden              | US-04  |
| POST   | `/{id}/promo`            | Aplicar código promocional   | US-07  |
| POST   | `/{id}/refund`           | Solicitar devolución         | US-13  |

### payment-service (`/api/payments`)
| Método | Ruta                     | Descripción                  | US     |
|--------|--------------------------|------------------------------|--------|
| POST   | `/webhook`               | Recibir callback pasarela    | US-05  |
| GET    | `/{orderId}`             | Estado de pago               | US-19  |

### ticket-service (`/api/tickets`)
| Método | Ruta                     | Descripción                  | US     |
|--------|--------------------------|------------------------------|--------|
| GET    | `/{id}`                  | Consultar boleta             | US-06  |
| POST   | `/{code}/validate`       | Validar QR en puerta         | RQ-05  |


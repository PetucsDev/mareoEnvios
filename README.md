# Mareo Envíos

Web Service RESTful para la gestión digital de envíos de mercadería a todo el país.

---

## Stack tecnológico

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.3.5 |
| PostgreSQL | 16 |
| Redis | 7 |
| Liquibase | incluido en Spring Boot |
| Swagger / OpenAPI | springdoc 2.6.0 |
| Maven | 3.9.x |
| Docker | multi-stage build |

---

## Arquitectura

El proyecto sigue una **arquitectura en capas** estricta:

```
Controller → Service (Interface) → ServiceImpl (extends BaseService) → Repository → DB
```

### Estructura de paquetes

```
sube.interviews.mareoenvios
├── config/              # CacheConfig, RetryConfig, OpenApiConfig (Jackson configurado via application.properties)
├── controller/          # CustomerController, ShippingController, ReportController
├── domain/              # Entidades JPA + ShippingState enum
├── dto/
│   ├── request/         # CustomerRequest, ShippingCreateRequest, ShippingItemRequest
│   └── response/        # CustomerResponse, ShippingResponse, ShippingItemResponse, TopProductResponse
├── event/               # ShippingStateChangedEvent, ShippingStateChangedListener
├── exception/           # Excepciones custom + GlobalExceptionHandler
├── mapper/              # CustomerMapper, ShippingMapper
├── repository/          # CustomerRepository, ShippingRepository, ProductRepository, ReportRepository
├── service/
│   ├── BaseService.java         # Abstract (Template Method)
│   ├── CustomerService.java     # Interface
│   ├── ShippingService.java     # Interface
│   ├── ReportService.java       # Interface
│   ├── ShippingWriteService.java    # Interface (escritura, ISP)
│   └── impl/                   # CustomerServiceImpl, ShippingServiceImpl, ReportServiceImpl, ShippingRetryWrapper
└── shipping/
    ├── factory/         # ShippingStateStrategyFactory
    └── state/           # ShippingStateStrategy + implementaciones por estado
```

---

## Patrones de diseño implementados

| Patrón | Ubicación | Propósito |
|---|---|---|
| **Strategy** | `shipping/state/` | Cada estado destino encapsula su lógica de transición. Agregar un nuevo estado solo requiere una nueva clase. |
| **Factory** | `shipping/factory/ShippingStateStrategyFactory` | Resuelve la Strategy correcta dado un estado en O(1) usando un Map inmutable construido al iniciar. |
| **Template Method** | `service/BaseService` | Define el flujo CRUD genérico. Las subclases sobreescriben hooks (`validateBeforeSave`, `validateBeforeDelete`) sin repetir la lógica base. |
| **Observer** | `event/` | Spring `ApplicationEventPublisher` desacopla los side-effects (logging/auditoría) del cambio de estado. |
| **Singleton** | `ShippingStateStrategyFactory`, todos los config beans | Un único bean por contexto, construido una sola vez con todas las strategies indexadas. |
| **Builder** | Entidades (`@Builder`), DTOs (`@Value @Builder`), `ShippingServiceImpl` | Construcción legible y segura de objetos complejos como `Shipping` y `ShippingResponse`. |

---

## Modelo de base de datos

![Esquema base de datos](assets/schema.png)

### Flujo de estados

![Flujo estados](assets/status-flow.png)

| Estado | Transiciones posibles |
|---|---|
| `INITIAL` | → `SENT_TO_MAIL`, → `CANCELLED` |
| `SENT_TO_MAIL` | → `IN_TRAVEL`, → `CANCELLED` |
| `IN_TRAVEL` | → `DELIVERED` |
| `DELIVERED` | — (estado final) |
| `CANCELLED` | — (estado final) |

---

## Endpoints

### Customer

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/customer/info/{customerId}` | Información de un comprador |
| `GET` | `/customer/info?page=0&size=20` | Listado paginado de compradores |

### Shipping

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/shipping/info/{shippingId}` | Información de un envío con detalle de productos |
| `GET` | `/shipping/info/{sendDateFrom}/{sendDateTo}?page=0&size=20` | Envíos paginados por rango de fechas (formato `yyyy-MM-dd`) |
| `GET` | `/shipping/info/state/{state}?page=0&size=20` | Listado paginado de envíos por estado |
| `POST` | `/shipping/create` | Crear solicitud de envío |
| `PATCH` | `/shipping/transition/sendToMail/{shippingId}` | Transición → SENT_TO_MAIL |
| `PATCH` | `/shipping/transition/inTravel/{shippingId}` | Transición → IN_TRAVEL |
| `PATCH` | `/shipping/transition/delivered/{shippingId}` | Transición → DELIVERED |
| `PATCH` | `/shipping/transition/cancelled/{shippingId}` | Transición → CANCELLED |

### Reports

| Método | Path | Descripción |
|---|---|---|
| `GET` | `/reports/topSended` | Top 3 productos más solicitados |

---

## Decisiones técnicas

### Cache (Redis)
Se implementa caché en los endpoints de lectura más frecuentes:
- `customers`, `customers-all` → TTL 15 min
- `shipping` → TTL 10 min
- `shippings-by-state`, `shippings-by-date` → TTL 5 min (datos más volátiles)
- `top-products` → TTL 30 min

Los métodos de escritura y transición invalidan las entradas relacionadas con `@CacheEvict`.

### Reintentos (@Retryable)
`createShipping` y `transitionState` tienen hasta **3 reintentos** con backoff exponencial (500ms, 1s, 2s) para tolerar fallos transitorios de infraestructura (DB, Redis).

La lógica de retry está en `ShippingRetryWrapper`, un bean separado de `ShippingServiceImpl`. Esto garantiza que `@Retryable` y `@Transactional` actúen en proxies distintos: el retry interceptor envuelve al transactional interceptor, de forma que cada reintento abre una transacción nueva y limpia.

Los reintentos se limitan a `DataAccessException` y `RedisConnectionFailureException`. Las excepciones de negocio (`BusinessException`, `ResourceNotFoundException`, `InvalidStateTransitionException`) son deterministas y **no se reintentan**.

El número de reintentos es configurable via la propiedad `spring.retry.max-attempts` en `application.properties`.

### Ruta `/shipping/info/state/{state}` vs. consigna
La consigna define `/shipping/info/{state}`, pero esa firma colisiona con `/shipping/info/{shippingId}`: ambos tienen un único segmento variable y Spring no puede distinguirlos en runtime cuando el valor es numérico (ej. `/shipping/info/11` es ambiguo entre un ID y un estado). Se agrega el segmento fijo `state/` para resolver la ambigüedad — `/shipping/info/state/INITIAL` — manteniendo el resto de la firma idéntico.

### Paginación
Todos los endpoints de listado devuelven `Page<T>` para soportar volúmenes de datos grandes. Los parámetros `page`, `size` y `sort` son opcionales (defaults: `page=0`, `size=20`). Esto aplica a `/customer/info`, `/shipping/info/state/{state}` y `/shipping/info/{from}/{to}`.

### Fetch lazy + JOIN FETCH
Todas las relaciones JPA usan `FetchType.LAZY`. Los queries que necesitan datos relacionados usan `JOIN FETCH` explícito en el repository para evitar N+1. Las queries paginadas con `JOIN FETCH` separan explícitamente la `countQuery` de la query de datos para evitar la advertencia `HHH90003004` de Hibernate (paginación en memoria).

### Segregación de interfaces (ISP)
`ShippingService` expone solo operaciones de **lectura**. Las operaciones de escritura (`createShipping`, `transitionState`) viven exclusivamente en `ShippingWriteService`. El controller inyecta `ShippingWriteService` apuntando al `ShippingRetryWrapper` via `@Qualifier`, sin acoplarse al bean concreto ni a su lógica de reintentos.

### Separación de responsabilidades en repositorios
`ReportRepository` es el repositorio exclusivo para consultas de reportes. La query de agregación `findTop3Products` vive ahí — no en `ShippingRepository` — manteniendo cada repositorio enfocado en su propia entidad raíz. `BaseService.findById()` y `save()` son `protected` ya que son helpers internos del patrón Template Method, no parte de la API pública de los servicios.

### Validación de rango de fechas
`@ValidDateRange` es un constraint personalizado (Bean Validation) aplicado a nivel de clase en `ShippingCreateRequest`. Valida que `sendDate` sea anterior o igual a `arriveDate` cuando ambas fechas están presentes. `arriveDate` es opcional: si no se provee, la validación pasa.

### Caché por perfil
`CacheConfig` (`@Profile("!test")`) configura el `RedisCacheManager` con TTLs específicos por caché. En el perfil `test`, esta clase no se carga y Spring Boot auto-configura un `SimpleCacheManager` gracias a `spring.cache.type=simple` en `src/test/resources/application.properties`, evitando la necesidad de un servidor Redis durante los tests.

### Liquibase
Se reemplaza `schema.sql`/`data.sql` por changelogs versionados:
- `001-create-schema.sql` — creación de tablas
- `002-insert-data.sql` — datos de ejemplo provistos por la consigna:
  - 3 customers (Marcos Gutierrez, Hernan Toledo, Silvina Hernandez)
  - 15 productos (Maiz Pisingallo, Tornillos, Modem, Celular, etc.)
  - 4 envíos con estados DELIVERED, IN_TRAVEL, INITIAL y CANCELLED
  - 9 items distribuidos en los 4 envíos

### Métricas
Disponibles en `/actuator/prometheus` para scraping con Prometheus/Grafana:
- JVM, sistema, HTTP requests, Hikari connection pool

---

## Cómo ejecutar

### Con Docker Compose (recomendado)

```bash
# Clonar el repo y pararse en la raíz
docker-compose up --build
```

La app queda disponible en `http://localhost:8080`.

### Local (requiere PostgreSQL y Redis corriendo)

```bash
# Variables de entorno (o editar application.properties)
export DB_HOST=localhost DB_PORT=5432 DB_NAME=mareo_envios
export DB_USERNAME=mareo_user DB_PASSWORD=mareo_pass
export REDIS_HOST=localhost REDIS_PORT=6379

mvn spring-boot:run
```

---

## Documentación interactiva

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

---

## Tests

```bash
# Ejecutar todos los tests (usa H2 en memoria, no requiere Docker)
mvn test
```

Tipos de tests incluidos:

**Unitarios** (Mockito / sin contexto Spring):
- `ShippingStateTest` — reglas de transición del enum (parametrizados)
- `ShippingStateStrategyTest` — todas las estrategias: happy path + transiciones inválidas
- `ShippingStateStrategyFactoryTest` — resolución de estrategia por estado y error sin estrategia registrada
- `CustomerMapperTest` — mapeo entity↔DTO y campos opcionales nulos
- `ShippingMapperTest` — mapeo de Shipping con items, lista vacía, toResponseList
- `GlobalExceptionHandlerTest` — cada tipo de excepción con su HTTP status y body
- `CustomerServiceImplTest` — getById, getAllCustomers paginado, not found
- `ShippingServiceImplTest` — getById, dateRange, byState, createShipping (customer existente, nuevo customer, errores), transitionState (happy path, inválido, not found)
- `ShippingRetryWrapperTest` — delegación al service y propagación de excepciones
- `ReportServiceImplTest` — top 3 productos, lista vacía, uso de PageRequest correcto

**Integración** (MockMvc + H2 + Liquibase + `spring.cache.type=simple`):
- `CustomerControllerIntegrationTest` — getById, listado paginado, not found
- `ShippingControllerIntegrationTest` — happy path, transiciones inválidas (422), errores de negocio (400), validación de fechas
- `ReportControllerIntegrationTest` — top 3, estructura de campos, orden descendente

---

## Variables de entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_HOST` | `localhost` | Host de PostgreSQL |
| `DB_PORT` | `5432` | Puerto de PostgreSQL |
| `DB_NAME` | `mareo_envios` | Nombre de la base de datos |
| `DB_USERNAME` | `mareo_user` | Usuario de la base de datos |
| `DB_PASSWORD` | `mareo_pass` | Contraseña de la base de datos |
| `REDIS_HOST` | `localhost` | Host de Redis |
| `REDIS_PORT` | `6379` | Puerto de Redis |
| `APP_PORT` | `8080` | Puerto de la aplicación |

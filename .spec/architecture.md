# Arquitectura – Plataforma de Pólizas con API Gateway

## 1. Principios de arquitectura

- **API Gateway como punto único de entrada**: desacoplar los clientes de la complejidad interna de microservicios.
- **Microservicios orientados al dominio**: separar responsabilidades en servicios de Pólizas, Riesgos, Notificaciones y Adapter con CORE.
- **Arquitectura hexagonal / ports & adapters** en cada servicio: aislar la lógica de dominio de frameworks, base de datos y sistemas externos.
- **Resiliencia y observabilidad de primer nivel**: todo consumo entre servicios debe ser observable y resistente a fallos.

## 2. Stack tecnológico

- **Backend**
  - Java 17+
  - Spring Boot (REST, Validation, Data JPA)
  - Spring Cloud Gateway (API Gateway reactivo)
  - Spring Cloud CircuitBreaker + Resilience4j
  - Spring Data JPA (MySQL)
  - Spring Data Redis + Spring Session (para rate limiting y sesión en el Gateway si se evoluciona a OAuth2)

- **Infraestructura**
  - MySQL 8 para persistencia de dominio (pólizas, riesgos).
  - Redis para rate limiting del Gateway y potencial manejo de sesión.
  - Docker / Docker Compose para orquestar servicios en local.

- **Seguridad y observabilidad (evolutivo)**
  - `x-api-key` en headers como seguridad mínima para la prueba.
  - Opcional: Keycloak como Identity Provider (OAuth2/OpenID) y Token Relay en el Gateway.
  - Micrometer Tracing + OpenTelemetry para trazas distribuidas.

## 3. Componentes lógicos

- **API Gateway (Spring Cloud Gateway)**
  - Expone `/api/...` hacia clientes.
  - Enruta a servicios de dominio (`polizas-service`, `riesgos-service`, `notificaciones-service`, `core-adapter`).
  - Aplica filtros transversales: seguridad (`x-api-key`), Circuit Breaker, Retry, Rate Limiting, Token Relay (si aplica).

- **Servicio de Pólizas**
  - Expone endpoints `/polizas` y operaciones de renovación/cancelación.
  - Contiene el modelo de dominio de Póliza y orquesta operaciones sobre Riesgos cuando corresponde (por ejemplo, cancelar todos los riesgos).

- **Servicio de Riesgos**
  - Gestiona el ciclo de vida de los riesgos asociados a pólizas colectivas.

- **Servicio de Notificaciones**
  - Gestiona el envío de correos/SMS para creación y renovación de pólizas.
  - Puede ser activado vía eventos (event-driven) o vía invocaciones síncronas.

- **Adapter de integración con CORE (core-adapter / core-mock)**
  - Expone un contrato estable para operaciones de actualización sobre el sistema CORE.
  - En la prueba, se implementa como `/core-mock/evento` que solo loguea la operación.

- **Base de datos**
  - Esquema relacional con tablas de Pólizas y Riesgos.

## 4. Patrones de arquitectura seleccionados

- **API Gateway**: concentrar seguridad, control de tráfico y observabilidad en un solo componente.
- **Microservicios**: separación de responsabilidades y escalabilidad independiente por dominio.
- **Hexagonal Architecture** en servicios de dominio: puertos (interfaces) para casos de uso y adaptadores para infraestructura (JPA, HTTP hacia CORE, colas).
- **Event-Driven (opcional para notificaciones)**: eventos de dominio como "PolizaCreada" y "PolizaRenovada" pueden publicarse en un bus de eventos para desacoplar la notificación.

## 5. Estructura de carpetas (ejemplo backend)

```text
/api-gateway
  src/main/java/...

/polizas-service
  src/main/java/com/empresa/polizas/
    domain/
    application/
    infrastructure/

riesgos-service
notificaciones-service
core-adapter

.Spec/
  spec.md
  architecture.md
  plan.md
  tasks.md
```

### API Gateway

El API Gateway se implementa con Spring Cloud Gateway y expone todos los endpoints externos bajo el prefijo `/api`.

Rutas obligatorias:

- `polizas-route`
  - Predicado: `Path=/api/polizas/**`
  - URI destino: `http://polizas-service:8080`
  - Filtros:
    - `StripPrefix=1` (elimina `/api` antes de llegar al servicio)

- `riesgos-route`
  - Predicado: `Path=/api/riesgos/**`
  - URI destino: `http://polizas-service:8080`   (los controladores de riesgos viven en el mismo microservicio)
  - Filtros:
    - `StripPrefix=1`

Cualquier implementación de `application.yml` del Gateway debe reflejar exactamente estas rutas. Una petición:

- `POST http://localhost:8080/api/riesgos/10/cancelar`

debe llegar al microservicio como:

- `POST http://polizas-service:8080/riesgos/10/cancelar`.

## 6. Configuración de rutas en el Gateway (vista conceptual)

Ejemplo de rutas en `application.yml` del Gateway:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: polizas-service
          uri: http://polizas-service:8080
          predicates:
            - Path=/api/polizas/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: polizasCB
                fallbackUri: forward:/fallback/polizas
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

## 7. Consideraciones de despliegue

- Uso de Docker Compose en ambientes locales para mantener la paridad de entornos.
- En entornos productivos, despliegue a Kubernetes con Ingress o Load Balancer delante del Gateway.
- Configuración externa vía variables de entorno y perfiles para URLs de servicios y credenciales.

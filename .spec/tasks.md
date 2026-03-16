# Tasks – Checklist atómico de implementación

> Marca cada ítem cuando esté completamente terminado y probado.

## Sección A – Preparación

- [x] Crear repositorio del proyecto.
- [x] Crear carpeta `.Spec/` en la raíz.
- [x] Copiar en `.Spec/` los archivos: `spec.md`, `architecture.md`, `plan.md`, `tasks.md`.
- [ ] Completar "Visión del producto" en `spec.md`.

## Sección B – Dominio y contratos

- [x] Modelar entidad `Poliza` con atributos: id, tipo, estado, fechas de vigencia, canonMensual, prima.
- [x] Modelar entidad `Riesgo` con atributos: id, polizaId, descripción, estado.
- [ ] Documentar todas las reglas de negocio en `spec.md`.
- [ ] Definir contratos JSON de request/response para cada endpoint en `spec.md`.

## Sección C – Servicio de Pólizas

- [x] Crear proyecto Spring Boot `polizas-service`.
- [x] Configurar conexión a MySQL.

- [x] Implementar entidad JPA `Poliza` según sección "🧬 Modelo de Dominio (Pólizas y Riesgos)" de `spec.md`.
- [x] Implementar entidad JPA `Riesgo` según sección "🧬 Modelo de Dominio (Pólizas y Riesgos)" de `spec.md`.
- [x] Crear `PolizaRepository` y `RiesgoRepository` (extends `JpaRepository`) con métodos para filtrar pólizas por tipo y estado.

- [x] Crear capas `controller`, `service`, `repository`.
- [x] Implementar `GET /polizas` con filtros por tipo y estado.
- [x] Implementar `GET /polizas/{id}/riesgos`.
- [x] Implementar `POST /polizas/{id}/renovar` con lógica de IPC y cambio de estado.
- [x] Implementar `POST /polizas/{id}/cancelar` cancelando también los riesgos.
- [x] Implementar `POST /polizas/{id}/riesgos` solo para pólizas colectivas.
- [x] Implementar `POST /riesgos/{id}/cancelar`.
- [x] Añadir validación de header `x-api-key`.
- [x] Implementar `/core-mock/evento` y log de envío.

## Sección D – API Gateway

- [x] Crear proyecto `api-gateway` con Spring Cloud Gateway.
- [x] Configurar rutas `/api/polizas/**` hacia `polizas-service`.
- [x] Añadir filtro global o por ruta para validar `x-api-key`.
- [ ] Configurar Circuit Breaker para la ruta de pólizas.
- [ ] Configurar Request Rate Limiter con Redis.

### Backend – API Gateway

- [x] Configurar ruta `polizas-route`:
  - Path: `/api/polizas/**`
  - URI: `http://polizas-service:8080`
  - Filtro: `StripPrefix=1`

- [x] Configurar ruta `riesgos-route`:
  - Path: `/api/riesgos/**`
  - URI: `http://polizas-service:8080`
  - Filtro: `StripPrefix=1`

- [x] Probar endpoints vía Gateway:
  - [x] `GET http://localhost:8080/api/polizas`
  - [x] `POST http://localhost:8080/api/polizas/1/renovar`
  - [x] `POST http://localhost:8080/api/riesgos/10/cancelar`
  - [x] `POST http://localhost:8080/api/core-mock/evento`

## Sección E – Resiliencia y observabilidad

- [x] Configurar Resilience4j (instancia de Circuit Breaker y, si aplica, Retry).
- [ ] Configurar Micrometer Tracing + exportador (Zipkin u otro).
- [ ] Verificar que las peticiones a través del Gateway generan trazas y métricas.

## Sección F – Docker y demo

- [x] Crear `docker-compose.yml` con mysql-db, redis, polizas-service y api-gateway.
- [x] Verificar que `docker-compose up` levanta todos los servicios.
- [x] Ejecutar un flujo completo (crear/renovar/cancelar póliza) a través del Gateway.
- [x] Validar que `/core-mock/evento` registra correctamente los logs de integración.

## Sección G – Cierre

- [ ] Actualizar `spec.md` con cualquier ajuste final.
- [ ] Actualizar `architecture.md` con la arquitectura implementada realmente.
- [ ] Actualizar `plan.md` indicando qué fases están completadas.
- [ ] Dejar `tasks.md` como evidencia del trabajo realizado y backlog futuro.

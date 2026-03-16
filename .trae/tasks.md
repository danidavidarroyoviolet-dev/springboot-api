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
- [ ] Crear capas `controller`, `service`, `repository`.
- [ ] Implementar `GET /polizas` con filtros por tipo y estado.
- [ ] Implementar `GET /polizas/{id}/riesgos`.
- [ ] Implementar `POST /polizas/{id}/renovar` con lógica de IPC y cambio de estado.
- [ ] Implementar `POST /polizas/{id}/cancelar` cancelando también los riesgos.
- [ ] Implementar `POST /polizas/{id}/riesgos` solo para pólizas colectivas.
- [ ] Implementar `POST /riesgos/{id}/cancelar`.
- [ ] Añadir validación de header `x-api-key`.
- [x] Implementar `/core-mock/evento` y log de envío.

## Sección D – API Gateway

- [x] Crear proyecto `api-gateway` con Spring Cloud Gateway.
- [x] Configurar rutas `/api/polizas/**` hacia `polizas-service`.
- [ ] Añadir filtro global o por ruta para validar `x-api-key`.
- [ ] Configurar Circuit Breaker para la ruta de pólizas.
- [ ] Configurar Request Rate Limiter con Redis.

### Backend – API Gateway

- [ ] Configurar ruta `polizas-route`:
  - Path: `/api/polizas/**`
  - URI: `http://polizas-service:8080`
  - Filtro: `StripPrefix=1`

- [ ] Configurar ruta `riesgos-route`:
  - Path: `/api/riesgos/**`
  - URI: `http://polizas-service:8080`
  - Filtro: `StripPrefix=1`

- [ ] Probar endpoints vía Gateway:
  - [ ] `GET http://localhost:8080/api/polizas`
  - [ ] `POST http://localhost:8080/api/polizas/1/renovar`
  - [ ] `POST http://localhost:8080/api/riesgos/10/cancelar`
  - [ ] `POST http://localhost:8080/api/core-mock/evento`

## Sección E – Resiliencia y observabilidad

- [ ] Configurar Resilience4j (instancia de Circuit Breaker y, si aplica, Retry).
- [ ] Configurar Micrometer Tracing + exportador (Zipkin u otro).
- [ ] Verificar que las peticiones a través del Gateway generan trazas y métricas.

## Sección F – Docker y demo

- [x] Crear `docker-compose.yml` con mysql-db, redis, polizas-service y api-gateway.
- [x] Verificar que `docker-compose up` levanta todos los servicios.
- [ ] Ejecutar un flujo completo (crear/renovar/cancelar póliza) a través del Gateway.
- [ ] Validar que `/core-mock/evento` registra correctamente los logs de integración.

## Sección G – Cierre

- [ ] Actualizar `spec.md` con cualquier ajuste final.
- [ ] Actualizar `architecture.md` con la arquitectura implementada realmente.
- [ ] Actualizar `plan.md` indicando qué fases están completadas.
- [ ] Dejar `tasks.md` como evidencia del trabajo realizado y backlog futuro.

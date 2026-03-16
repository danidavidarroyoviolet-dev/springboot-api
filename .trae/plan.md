# Plan de Implementación – Spec-Driven Development

## Objetivo

Definir fases claras de implementación que permitan avanzar desde el diseño de dominio hasta una demo funcional con API Gateway, servicios de negocio y orquestación por contenedores.

## Fase 0 – Preparación y alineación

- [x] Crear repositorio del proyecto.
- [x] Crear carpeta `.Spec/` con los archivos `spec.md`, `architecture.md`, `plan.md`, `tasks.md`.
- [ ] Completar la sección "Visión del producto" en `spec.md`.

## Fase 1 – Modelo de dominio y contrato del API

- [x] Definir entidades de dominio principales: Poliza y Riesgo.
- [ ] Validar y refinar reglas de negocio en `spec.md`.
- [x] Diseñar los contratos de los endpoints REST (paths, métodos, request/response) y documentarlos en `spec.md`.

## Fase 2 – Servicio de Pólizas (Módulo 2 de la prueba)

- [x] Crear proyecto Spring Boot `polizas-service`.
- [ ] Implementar entidades JPA, repositorios y servicios de dominio.
- [ ] Implementar endpoints:
  - `GET /polizas`
  - `GET /polizas/{id}/riesgos`
  - `POST /polizas/{id}/renovar`
  - `POST /polizas/{id}/cancelar`
  - `POST /polizas/{id}/riesgos`
  - `POST /riesgos/{id}/cancelar`
- [ ] Implementar validaciones de negocio y seguridad vía `x-api-key`.
- [x] Implementar endpoint `/core-mock/evento` y la integración desde las operaciones de dominio.

## Fase 3 – API Gateway e integración

- [x] Crear proyecto `api-gateway` con Spring Cloud Gateway.
- [x] Definir rutas para exponer `/api/polizas/**` y delegar al `polizas-service` (usando `StripPrefix=1`).
- [ ] Configurar filtros de seguridad (validación de `x-api-key`), Circuit Breaker y Rate Limiter.

## Fase 4 – Resiliencia y observabilidad

- [ ] Configurar Resilience4j (Circuit Breaker, Retry) en el Gateway.
- [ ] Configurar Micrometer Tracing y exportación de trazas (Zipkin u otra herramienta).
- [ ] Asegurar logs estructurados y métricas básicas (latencias, errores, throughput).

## Fase 5 – Orquestación con Docker Compose

- [x] Crear `docker-compose.yml` con los servicios:
  - `mysql-db`
  - `redis`
  - `polizas-service`
  - `api-gateway`
  - (opcional) `keycloak`, `zipkin`, `notificaciones-service`.
- Nota: mapeo de MySQL expuesto como `3307:3306` para evitar conflicto con MySQL local.
- [ ] Verificar que, levantando los contenedores, pueda probarse el flujo completo:
  - Crear/renovar/cancelar póliza.
  - Ver en logs el consumo del `/core-mock/evento`.

## Fase 6 – Endurecimiento y documentación

- [ ] Revisar y ajustar `spec.md` con lo aprendido durante la implementación.
- [ ] Completar `README` del proyecto con instrucciones de ejecución.
- [ ] Actualizar `tasks.md` marcando tareas completadas y generando backlog de mejoras.

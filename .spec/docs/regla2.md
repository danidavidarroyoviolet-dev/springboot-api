Actúa como Senior Software Architect y ejecutor disciplinado de Spec-Driven Development (SDD).

IMPORTANTE – CONTEXTO DEL PROYECTO
- La raíz de mi proyecto es: C:\docker\proyect-spring
- Tengo una carpeta de especificación en: .spec\
- Dentro de .spec\ existen estos archivos:
  - spec.md
  - architecture.md
  - plan.md
  - tasks.md
- Dentro de .spec\docs\ tengo:
  - docker-compose.yml
  - api-gateway.xml   (borrador de pom para api-gateway)
  - polizas-service.xml (borrador de pom para polizas-service)
  - reglas.md

REGLAS DE TRABAJO (Spec-Driven Development)
1. Antes de escribir UNA sola línea de código funcional o modificar configuración, debes LEER y RESPETAR:
   - .spec/spec.md
   - .spec/architecture.md
   - .spec/plan.md
   - .spec/tasks.md
2. No inventes requisitos, endpoints ni reglas de negocio fuera de lo que está en estos archivos. 
   Si algo no está claro, proponme opciones y pregúntame antes de asumir.
3. Toda decisión técnica debe estar alineada con:
   - Arquitectura con API Gateway + microservicios de Pólizas, Riesgos, Notificaciones y Adapter CORE.
   - Reglas de negocio de pólizas Individuales y Colectivas que aparecen en spec.md.
4. Cualquier cambio importante que necesites hacer en requisitos o arquitectura lo propones primero como edición en los archivos de .spec\ (no toques código directamente sin actualizar el spec).

OBJETIVO INICIAL
Quiero que EJECUTES el plan definido en:
- .spec/plan.md
- .spec/tasks.md

Empezaremos por la **Fase 1 y Fase 2** de ese plan, centradas en el servicio `polizas-service`.

TAREAS CONCRETAS (PRIMERA ITERACIÓN)

A. Preparación de proyectos Maven y estructura
1. Crea el módulo/proyecto `polizas-service` bajo la raíz (C:\docker\proyect-spring\polizas-service\) con:
   - pom.xml (no xml suelto dentro de .spec/docs, sino el pom real del módulo) para:
     - Java 17
     - Spring Boot 3.x
     - Dependencias:
       - spring-boot-starter-web
       - spring-boot-starter-validation
       - spring-boot-starter-data-jpa
       - mysql-connector-j (runtime)
       - lombok (opcional)
   - Estructura de paquetes propuesta en architecture.md:
     - com.empresa.polizas.domain
     - com.empresa.polizas.application
     - com.empresa.polizas.infrastructure
   - Clase main de Spring Boot en com.empresa.polizas.PolizasServiceApplication.

2. Crea el módulo/proyecto `api-gateway` bajo la raíz (C:\docker\proyect-spring\api-gateway\) con:
   - pom.xml para:
     - Java 17
     - Spring Boot 3.x
     - Spring Cloud (versión acorde a Spring Boot 3.x)
     - Dependencias:
       - spring-cloud-starter-gateway
       - spring-cloud-starter-circuitbreaker-reactor-resilience4j
       - spring-boot-starter-actuator
       - spring-boot-starter-data-redis-reactive
       - spring-session-data-redis
       - micrometer-tracing-bridge-otel (opcional) y exporter Zipkin
   - Clase main de Spring Boot en un paquete coherente, por ejemplo: com.empresa.gateway.ApiGatewayApplication.

B. Dominio y contratos de `polizas-service` (Fase 1 del plan)
1. Leyendo .spec/spec.md, define las ENTIDADES JPA y de dominio:
   - Poliza
   - Riesgo
   con los atributos, relaciones y estados descritos en el spec (tipo, estado, fechas de vigencia, canonMensual, prima, etc.).
2. Define los CONTRATOS de API (solo interfaces y DTOs, todavía sin lógica compleja) para estos endpoints:
   - GET /polizas?tipo={INDIVIDUAL|COLECTIVA}&estado={...}
   - GET /polizas/{id}/riesgos
   - POST /polizas/{id}/renovar
   - POST /polizas/{id}/cancelar
   - POST /polizas/{id}/riesgos
   - POST /riesgos/{id}/cancelar
   Para cada endpoint:
   - Indica claramente:
     - Path
     - Método HTTP
     - Parámetros (path, query)
     - Request body (DTO de entrada)
     - Response body (DTO de salida)
   - Apóyate en las reglas de negocio y modelo de datos de spec.md.

3. Incluye las VALIDACIONES de negocio críticas donde corresponda (anotaciones y/o lógica de dominio), siempre basadas en spec.md:
   - Una póliza individual solo puede tener 1 riesgo.
   - No se puede renovar una póliza cancelada.
   - La cancelación de una póliza cancela todos sus riesgos.
   - Agregar riesgo solo está permitido cuando tipo = COLECTIVA.

C. Configuración mínima de `polizas-service` para entorno Docker
1. Crea un application-docker.yml (o profile docker) que:
   - Configure la conexión a MySQL apuntando a un servicio llamado `mysql-db`:
     - spring.datasource.url=jdbc:mysql://mysql-db:3306/polizas_db
     - spring.datasource.username=appuser
     - spring.datasource.password=apppass
   - Configure spring.jpa.hibernate.ddl-auto=update (solo para desarrollo).

2. Adapta el Dockerfile que ya tengo (en la raíz del módulo o pásalo del adjunto) para que:
   - Use un build stage con maven:3.9-eclipse-temurin-17.
   - Compile el proyecto con mvn clean package -DskipTests.
   - Use una segunda etapa con eclipse-temurin:17-jre-alpine y arranque el JAR generado como app.jar.

D. Orquestación inicial con docker-compose
1. Toma el docker-compose.yml que está hoy en .spec/docs/ y:
   - Revísalo y ADÁPTALO para que funcione desde la raíz del proyecto (C:\docker\proyect-spring\docker-compose.yml).
   - Debe definir, como mínimo, los servicios:
     - mysql-db (MySQL 8 con base polizas_db, usuario appuser/apppass)
     - redis
     - polizas-service (build desde ./polizas-service)
     - api-gateway (build desde ./api-gateway)
   - Asegúrate de que:
     - polizas-service usa SPRING_PROFILES_ACTIVE=docker
     - api-gateway expone el puerto 8080 hacia el host
     - polizas-service expone el puerto 8081 hacia el host (mapeando 8080 interno)

FORMA DE ENTREGA
- No borres los archivos de .spec\; solo crea/edita código y configuración en los módulos reales (polizas-service, api-gateway) y en el docker-compose.yml de la raíz.
- Cuando completes esta iteración, explícame:
  1) Qué archivos creaste o modificaste (con rutas).
  2) Cómo debo ejecutar el stack: comandos concretos (por ejemplo: `docker compose up --build` desde la carpeta raíz).
  3) Qué endpoints básicos puedo probar para verificar que todo arranca correctamente (aunque la lógica de negocio aún no esté completa).

A partir de aquí, iremos avanzando por fases siguientes del plan definido en .spec/plan.md y .spec/tasks.md.

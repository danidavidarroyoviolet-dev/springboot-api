# Plataforma de Gestión de Pólizas (API Gateway + Microservicios)

Este proyecto implementa una plataforma distribuida para la gestión de pólizas de arrendamiento (individuales y colectivas) y sus riesgos asociados. Está construido con **Spring Boot 3**, **Spring Cloud Gateway**, **MySQL** y **Redis**, orquestado mediante **Docker Compose**.

## 🚀 Arquitectura

El sistema sigue una arquitectura de microservicios expuesta a través de un API Gateway único.

- **API Gateway (Puerto 8080)**: Punto de entrada único. Enruta peticiones, maneja seguridad y rate limiting.
- **Polizas Service (Puerto interno 8081)**: Microservicio de dominio que gestiona Pólizas y Riesgos.
- **MySQL (Puerto 3307)**: Base de datos relacional para persistencia.
- **Redis (Puerto 6379)**: Cache y gestión de Rate Limiting para el Gateway.

## 🛠 Tecnologías

- **Java 17** (Eclipse Temurin)
- **Spring Boot 3.2.x** (Web, Validation, Data JPA)
- **Spring Cloud Gateway**
- **Docker & Docker Compose**
- **MySQL 8.0**
- **Redis 6**

## 📋 Endpoints Disponibles

Todos los endpoints se consumen a través del **API Gateway** en el puerto `8080` bajo el prefijo `/api`.

**Base URL:** `http://localhost:8080/api`

### Headers Requeridos
```http
Content-Type: application/json
x-api-key: 123456  (Pendiente de validación estricta)
```

### 1. Gestión de Pólizas (`/api/polizas`)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **GET** | `/polizas` | Lista todas las pólizas (permite filtros `?tipo=X&estado=Y`). |
| **GET** | `/polizas/{id}/riesgos` | Obtiene los riesgos asociados a una póliza. |
| **POST** | `/polizas/{id}/renovar` | Renueva una póliza (incrementa canon por IPC). |
| **POST** | `/polizas/{id}/cancelar` | Cancela una póliza y todos sus riesgos. |
| **POST** | `/polizas/{id}/riesgos` | Agrega un riesgo a una póliza (Solo Colectivas). |

### 2. Gestión de Riesgos (`/api/riesgos`)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/riesgos/{id}/cancelar` | Cancela un riesgo individualmente. |

### 3. Mock de Integración (`/api/core-mock`)

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| **POST** | `/core-mock/evento` | Simula el envío de eventos al CORE legado (solo logs). |

---

## ⚙️ Cómo ejecutar el proyecto

La forma recomendada es utilizar **Docker Compose**, ya que levanta la infraestructura completa (BD, Redis y Apps) automáticamente.

### Prerrequisitos
- Docker y Docker Compose instalados.
- Java 17 (opcional, solo si quieres ejecutar sin Docker).

### Pasos

1. **Clonar el repositorio**:
   ```bash
   git clone <url-del-repo>
   cd proyect-spring
   ```

2. **Levantar el stack completo**:
   ```bash
   docker compose up --build
   ```
   *Esto compilará los proyectos `polizas-service` y `api-gateway` y levantará los contenedores.*

3. **Verificar servicios**:
   - API Gateway: `http://localhost:8080/actuator/health` (si actuator está activo) o prueba un endpoint.
   - MySQL: `localhost:3307` (Usuario: `appuser`, Password: `apppass`).

### Pruebas rápidas (cURL)

**Listar pólizas:**
```bash
curl -X GET http://localhost:8080/api/polizas
```

**Simular renovación:**
```bash
curl -X POST http://localhost:8080/api/polizas/1/renovar \
  -H "Content-Type: application/json" \
  -d '{"ipc": 5.2}'
```

**Probar Mock CORE:**
```bash
curl -X POST http://localhost:8080/api/core-mock/evento \
  -H "Content-Type: application/json" \
  -d '{"evento": "ACTUALIZACION", "polizaId": 555}'
```

## 📂 Estructura del Proyecto

```text
/
├── api-gateway/         # Proyecto Spring Cloud Gateway
├── polizas-service/     # Microservicio de Dominio (Pólizas + Riesgos)
├── .Spec/               # Documentación SDD (Spec-Driven Development)
├── docker-compose.yml   # Orquestación raíz
└── README.md            # Este archivo
```

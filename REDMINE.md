# API de Gestión de Pólizas

Proyecto de ejemplo para la prueba técnica de Desarrollador TI/Senior. Implementa una API para la gestión de pólizas de arrendamiento (individuales y colectivas) y sus riesgos asociados.

## Tecnologías

- Java 17
- Spring Boot (Web, Validation, Data JPA)
- Base de datos relacional (PostgreSQL / MySQL / H2)
- Maven o Gradle
- Docker (opcional)

## Descripción funcional

El sistema gestiona dos tipos de pólizas:

- **Individual**: una sola póliza asociada a un arrendatario; solo puede tener **un** riesgo.
- **Colectiva**: asociada a inmobiliarias o administraciones de copropiedades; puede tener **uno o muchos** riesgos.

Todas las pólizas tienen:

- Periodo de vigencia
- Canon mensual de arrendamiento
- Valor de la prima (canon mensual × número de meses de vigencia)
- Posibilidad de renovación, ajustando el canon según el incremento del IPC

### Reglas de negocio principales

- Una póliza **individual** solo puede tener **1 riesgo**.
- No se puede **renovar** una póliza **cancelada**.
- La **cancelación** de una póliza cancela **todos** sus riesgos asociados.
- Agregar un riesgo exige validar el **tipo de póliza**.
- La renovación incrementa canon y prima con el factor de **IPC** y cambia el estado a `RENOVADA`.

## Endpoints

Todos los endpoints requieren el header de seguridad:

```http
x-api-key: 123456
```

### 1. Listar pólizas

```text
GET /polizas?tipo={tipo}&estado={estado}
```

**Descripción:** Lista pólizas filtrando por tipo (INDIVIDUAL, COLECTIVA) y estado.

**Parámetros:**
- `tipo` (opcional)
- `estado` (opcional)

**Respuesta:** Lista de pólizas (JSON).

### 2. Listar riesgos de una póliza

```text
GET /polizas/{id}/riesgos
```

**Descripción:** Obtiene los riesgos asociados a la póliza indicada.

**Parámetros de ruta:**
- `id`: identificador de la póliza.

### 3. Renovar una póliza

```text
POST /polizas/{id}/renovar
```

**Descripción:** Renueva una póliza, incrementando canon y prima en función del IPC y cambiando el estado a RENOVADA.

**Validaciones:**
- La póliza no debe estar cancelada.

**Cuerpo (ejemplo):**

```json
{
  "ipc": 0.10
}
```

### 4. Cancelar una póliza

```text
POST /polizas/{id}/cancelar
```

**Descripción:** Cancela una póliza y todos sus riesgos asociados.

**Efectos:**
- Estado de la póliza pasa a CANCELADA.
- Riesgos asociados pasan a estado cancelado.

### 5. Agregar riesgo a una póliza

```text
POST /polizas/{id}/riesgos
```

**Descripción:** Agrega un riesgo a una póliza solo si es Colectiva.

**Validaciones:**
- Si la póliza es INDIVIDUAL, no se permite agregar más de un riesgo.
- Si la póliza es COLECTIVA, puede tener múltiples riesgos.

**Cuerpo (ejemplo):**

```json
{
  "descripcion": "Canon de arrendamiento",
  "montoAsegurado": 2500000
}
```

### 6. Cancelar un riesgo

```text
POST /riesgos/{id}/cancelar
```

**Descripción:** Cancela un riesgo específico asociado a una póliza.

## Mock de integración con CORE

Se expone un endpoint mock que simula el envío de eventos al CORE transaccional legado.

```text
POST /core-mock/evento
Content-Type: application/json
x-api-key: 123456
```

**Cuerpo (ejemplo):**

```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 555
}
```

**Descripción:** No realiza lógica de negocio, solo registra en logs que se intentó enviar el evento al CORE.

## Arquitectura del proyecto

Estructura por capas:

- **controller**: controladores REST (exponen los endpoints).
- **service**: lógica de negocio, validaciones y orquestación.
- **repository**: acceso a datos (interfaces JPA / consultas).
- **model/entity**: entidades del dominio (Poliza, Riesgo).
- **dto**: objetos de transferencia para request/response.
- **config**: configuración (seguridad simple para x-api-key, etc.).

Ejemplo de estructura de paquetes:

```text
src/main/java/com/empresa/polizas
 ├── controller
 ├── service
 ├── repository
 ├── model
 │    ├── entity
 │    └── enums
 ├── dto
 └── config
```

## Seguridad

La API implementa una validación mínima mediante un filtro/interceptor:

- Verifica la presencia y el valor del header `x-api-key`.
- Si el header no está presente o es incorrecto, responde con `401 Unauthorized`.

## Cómo ejecutar el proyecto

### Prerrequisitos

- Java 17
- Maven o Gradle
- Docker (opcional)

### Ejecución local

```bash
# Clonar el repositorio
git clone https://github.com/tu-usuario/tu-repo-polizas.git 
cd tu-repo-polizas

# Compilar y ejecutar
mvn spring-boot:run
# o
./mvnw spring-boot:run
```

La API quedará expuesta típicamente en:

```text
http://localhost:8080
```

### Ejecución con Docker (opcional)

```bash
docker build -t api-polizas .
docker run -p 8080:8080 api-polizas
```

### Pruebas

Puedes usar Postman o cURL para probar los endpoints. Ejemplo:

```bash
curl -H "x-api-key: 123456" http://localhost:8080/polizas
```

## Pendiente / Mejoras futuras

- Autenticación y autorización más robustas (JWT / OAuth2).
- Manejo avanzado de errores y códigos de estado.
- Pruebas unitarias e integrales.
- Documentación OpenAPI/Swagger.

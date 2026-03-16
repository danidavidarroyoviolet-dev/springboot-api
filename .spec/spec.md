# Especificación de Negocio – Plataforma de Gestión de Pólizas

> **Nota:** En la sección "Visión del producto" debes describir con tus propias palabras el objetivo del sistema, el contexto de la compañía y cualquier restricción adicional que no aparezca en la prueba.

## 1. Visión del producto (a completar por el autor)

- **¿Qué problema de negocio resuelve la plataforma de gestión de pólizas?**
    > *[Espacio reservado para tu respuesta]*
- **¿Quiénes son los usuarios principales (front de agentes, backoffice, integraciones externas)?**
    > *[Espacio reservado para tu respuesta]*
- **¿Qué métricas de éxito de negocio se quieren impactar (reducción de tiempo de emisión, menor tasa de errores, etc.)?**
    > *[Espacio reservado para tu respuesta]*

## 2. Contexto de negocio

El negocio gestiona pólizas de arrendamiento de inmuebles de dos tipos:

- **Pólizas individuales**: el tomador y asegurado es el arrendatario; el beneficiario es el arrendador.
- **Pólizas colectivas**: orientadas a inmobiliarias y administraciones de copropiedades; se aseguran los arrendatarios de múltiples inmuebles y los beneficiarios son los arrendadores.

### Reglas estructurales clave:

- Una póliza colectiva puede tener **uno o muchos** riesgos.
- Una póliza individual solo puede tener **un** riesgo.
- Todas las pólizas tienen:
    - Periodo de vigencia.
    - Canon mensual de arrendamiento.
    - Prima calculada como `canon × meses de vigencia`.
- Las pólizas pueden renovarse por el mismo periodo, ajustando el canon según el incremento del IPC.

## 3. Objetivos funcionales del sistema

La plataforma debe exponer un API para la gestión de pólizas que permita, como mínimo:

1. Crear, consultar y modificar pólizas (individuales y colectivas).
2. Gestionar riesgos de pólizas colectivas (agregar, cancelar riesgos).
3. Soportar la consulta y operaciones necesarias para frontends que consumen pólizas individuales.
4. Renovación automática o manual de pólizas basada en reglas de negocio (IPC, estado actual, vigencia).
5. Disparar eventos de notificación (correo/SMS) para creación y renovación de pólizas.
6. Integrarse con un CORE transaccional legado mediante un servicio agnóstico de edición en una capa media (weblogic / core-mock).
7. Garantizar disponibilidad 24/7 y resiliencia ante fallos de componentes individuales.

## 4. Endpoints mínimos requeridos (Hands-on)

Nota: cuando se consumen a través del API Gateway, los endpoints se exponen con base path `/api`.

### 4.1. Gestión de pólizas

- `GET /polizas?tipo={INDIVIDUAL|COLECTIVA}&estado={...}`
  - Lista pólizas filtrando por tipo y estado.

- `GET /polizas/{id}/riesgos`
  - Devuelve lista de `Riesgo` activos y cancelados de la póliza

- `POST /polizas/{id}/renovar`
  - Body: `{"ipc": 5.2}` (porcentaje)
  - Valida que no esté CANCELADA (RB-002)
  - `canonMensual` = `canonMensual × (1 + ipc/100)`
  - `prima` = nuevo canon × meses originales
  - Fechas de vigencia avanzan al siguiente periodo
  - Estado → `RENOVADA`
  - Llama `/core-mock/evento`

- `POST /polizas/{id}/cancelar`
  - Cancela la póliza, aplicando reglas de negocio asociadas.

### 4.2. Gestión de riesgos

- `POST /polizas/{id}/riesgos`
  - Body: Riesgo con `descripcion`
  - **Solo si** tipo=COLECTIVA (RB-004), sino error 400
  - Riesgo nuevo estado=ACTIVO
  - Llama `/core-mock/evento`

- `POST /riesgos/{id}/cancelar`
  - Cambia estado del riesgo a CANCELADO (RB-005)
  - Llama `/core-mock/evento` de la póliza padre

### 4.3. Integración con CORE (mock)

- `POST /core-mock/evento`
  - Request body:
    ```json
    {
      "evento": "ACTUALIZACION",
      "polizaId": 555
    }
    ```
  - Su objetivo es registrar en logs que se intentó enviar la operación al CORE.

### 4.4. Seguridad

**Seguridad en API Gateway** (implementada)
- Filtro global `GlobalApiKeyFilter` para paths `/api/**`
- Valida `x-api-key: 123456` antes de rutear a `polizas-service`
- Respuesta 401 si inválida

## 5. Reglas de negocio

- **RB-001 – Riesgos por tipo de póliza**
  - Una póliza INDIVIDUAL solo puede tener 1 riesgo.
  - Una póliza COLECTIVA puede tener uno o muchos riesgos.

- **RB-002 – Renovación de póliza**
  - Ejemplo: canon=1000, ipc=5 → nuevo canon=1050
  - Prima se recalcula con mismos meses
  - Fechas avanzan: 2026-01→2026-06 → 2026-07→2026-12
  - No se puede renovar una póliza en estado CANCELADA.
  - La renovación recibe un parámetro `ipc` (porcentaje).
  - Al renovar:
    - `canonMensual` = `canonMensual` × (1 + ipc/100).
    - `prima` se recalcula con el nuevo canon y la misma cantidad de meses de vigencia.
    - El estado pasa a RENOVADA.
    - Las fechas de vigencia se mueven al siguiente periodo con la misma duración original.

- **RB-003 – Cancelación de póliza**
  - Al cancelar una póliza:
    - El estado de la póliza pasa a CANCELADA.
    - Todos los riesgos asociados pasan a estado CANCELADO (cascada).

- **RB-004 – Alta de riesgo**
  - Solo se pueden crear riesgos en pólizas de tipo COLECTIVA.
  - Intentar agregar un riesgo a una póliza INDIVIDUAL debe devolver un error de negocio.

- **RB-005 – Cancelación de riesgo**
  - `POST /riesgos/{id}/cancelar` cambia el estado del riesgo a CANCELADO.

## 🧬 Modelo de Dominio (Pólizas y Riesgos)

### Entidad Poliza

- `id`: Long
- `tipo`: enum { INDIVIDUAL, COLECTIVA }
- `estado`: enum { VIGENTE, RENOVADA, CANCELADA }
- `fechaInicioVigencia`: LocalDate
- `fechaFinVigencia`: LocalDate
- `canonMensual`: BigDecimal
- `prima`: BigDecimal (canonMensual × número de meses de vigencia)
- Relación con Riesgos:
  - Para tipo INDIVIDUAL: máximo 1 riesgo asociado.
  - Para tipo COLECTIVA: 1 a N riesgos asociados.

### Entidad Riesgo

- `id`: Long
- `descripcion`: String
- `estado`: enum { ACTIVO, CANCELADO }
- `poliza`: referencia a Poliza (FK poliza_id)

> Nota: Este modelo se implementará con JPA/Hibernate en el microservicio `polizas-service`.

## 6. Modelo de datos de alto nivel

**Request POST /polizas/{id}/renovar:**
```json
{
  "ipc": 5.2
}
```

**Request POST /polizas/{id}/riesgos:**
```json
{
  "descripcion": "Inmueble Calle 123 #45-67"
}
```

### Entidades principales (vista conceptual, sin SQL detallado):

**Poliza**
- `id`
- `tipo` (INDIVIDUAL, COLECTIVA)
- `estado` (VIGENTE, RENOVADA, CANCELADA, etc.)
- `fechaInicioVigencia`
- `fechaFinVigencia`
- `canonMensual`
- `prima`
- datos de tomador, asegurado y beneficiario según tipo

**Riesgo**
- `id`
- `polizaId` (FK a Poliza)
- `descripcion` / detalle del riesgo
- `estado` (ACTIVO, CANCELADO)

**EventoNotificacion** (opcional, para trazabilidad de notificaciones)
- `id`
- `polizaId`
- `tipoEvento` (CREACION, RENOVACION, CANCELACION)
- `canal` (EMAIL, SMS)
- `fechaEnvio`
- `estadoEnvio`

## 7. Requisitos no funcionales

- **Disponibilidad**: 24/7, con tolerancia a fallos en servicios dependientes.
- **Escalabilidad**: el sistema debe escalar horizontalmente a nivel de microservicios y API Gateway.
- **Resiliencia**: uso de patrones como Circuit Breaker, Retry y Rate Limiting en el Gateway.
- **Observabilidad**: logging estructurado, métricas y trazas distribuidas (Micrometer Tracing + OpenTelemetry).
- **Seguridad**: autenticación/autorización en el Gateway (evolucionable a OAuth2/OpenID Connect) y validación de `x-api-key` para la prueba.

## 8. Fuera de alcance (para esta iteración)

- Integración real con un CORE de seguros productivo.
- Motor de reglas de negocio complejo para suscripción de riesgos.
- Frontend completo de autoservicio.

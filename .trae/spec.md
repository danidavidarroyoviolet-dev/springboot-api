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
  - Devuelve los riesgos asociados a la póliza.

- `POST /polizas/{id}/renovar`
  - Incrementa canon y prima en función de `+IPC`.
  - Cambia el estado a `RENOVADA` si la operación es válida.

- `POST /polizas/{id}/cancelar`
  - Cancela la póliza, aplicando reglas de negocio asociadas.

### 4.2. Gestión de riesgos

- `POST /polizas/{id}/riesgos`
  - Agrega un riesgo a una póliza **solo si** el tipo es Colectiva.

- `POST /riesgos/{id}/cancelar`
  - Cancela un riesgo individual.

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

### 4.4. Seguridad mínima
Todas las peticiones al API deben incluir el header obligatorio: `x-api-key: 123456`.

## 5. Reglas de negocio

- **Pólizas Individuales**: Solo pueden tener 1 riesgo.
- **Renovación**:
    - No se puede renovar una póliza cancelada.
    - Se ajusta el canon aplicando el IPC configurado para el periodo.
    - La prima se recalcula como `canon_actualizado × meses_vigencia`.
- **Cancelación**: La cancelación de una póliza cancela todos sus riesgos.
- **Agregar Riesgo**: Exige validación del tipo de póliza (solo Colectiva).

## 6. Modelo de datos de alto nivel

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

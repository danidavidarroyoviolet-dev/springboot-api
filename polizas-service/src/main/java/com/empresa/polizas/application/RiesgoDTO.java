package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoRiesgo;
import lombok.Data;

@Data
public class RiesgoDTO {
    private Long id;
    private Long polizaId;
    private String descripcion;
    private EstadoRiesgo estado;
}

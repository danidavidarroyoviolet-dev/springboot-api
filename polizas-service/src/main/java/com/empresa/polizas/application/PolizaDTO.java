package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoPoliza;
import com.empresa.polizas.domain.TipoPoliza;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PolizaDTO {
    private Long id;
    private TipoPoliza tipo;
    private EstadoPoliza estado;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Double canonMensual;
    private Double prima;
}

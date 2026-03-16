package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoPoliza;
import com.empresa.polizas.domain.TipoPoliza;
import lombok.Data;
import java.time.LocalDate;
import java.math.BigDecimal;

@Data
public class PolizaDTO {
    private Long id;
    private TipoPoliza tipo;
    private EstadoPoliza estado;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private BigDecimal canonMensual;
    private BigDecimal prima;
}

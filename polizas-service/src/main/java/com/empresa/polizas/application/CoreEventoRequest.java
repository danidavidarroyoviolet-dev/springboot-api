package com.empresa.polizas.application;

import lombok.Data;

@Data
public class CoreEventoRequest {
    private String evento;
    private Long polizaId;
}

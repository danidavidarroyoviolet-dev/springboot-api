package com.empresa.polizas.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarRiesgo(@PathVariable Long id) {
        // TODO: Implementar lógica de cancelación
        return ResponseEntity.ok().build();
    }
}

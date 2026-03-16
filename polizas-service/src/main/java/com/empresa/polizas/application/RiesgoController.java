package com.empresa.polizas.application;

import com.empresa.polizas.domain.Riesgo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/riesgos")
public class RiesgoController {

    private final RiesgoService riesgoService;

    public RiesgoController(RiesgoService riesgoService) {
        this.riesgoService = riesgoService;
    }

    // 9. POST /riesgos/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Riesgo> cancelarRiesgo(@PathVariable Long id) {
        Riesgo riesgoCancelado = riesgoService.cancelarRiesgo(id);
        return ResponseEntity.ok(riesgoCancelado);
    }
}

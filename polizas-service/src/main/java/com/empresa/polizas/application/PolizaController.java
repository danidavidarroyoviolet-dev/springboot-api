package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoPoliza;
import com.empresa.polizas.domain.TipoPoliza;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    @GetMapping
    public ResponseEntity<List<PolizaDTO>> listarPolizas(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        // TODO: Implementar lógica de búsqueda
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<RiesgoDTO>> listarRiesgos(@PathVariable Long id) {
        // TODO: Implementar lógica de búsqueda
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/{id}/renovar")
    public ResponseEntity<PolizaDTO> renovarPoliza(@PathVariable Long id, @RequestBody RenovacionRequest request) {
        // TODO: Implementar lógica de renovación
        return ResponseEntity.ok(new PolizaDTO());
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarPoliza(@PathVariable Long id) {
        // TODO: Implementar lógica de cancelación
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/riesgos")
    public ResponseEntity<RiesgoDTO> agregarRiesgo(@PathVariable Long id, @RequestBody NuevoRiesgoRequest request) {
        // TODO: Implementar lógica de agregar riesgo
        return ResponseEntity.ok(new RiesgoDTO());
    }
}

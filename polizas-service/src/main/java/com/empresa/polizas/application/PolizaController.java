package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoPoliza;
import com.empresa.polizas.domain.Poliza;
import com.empresa.polizas.domain.Riesgo;
import com.empresa.polizas.domain.TipoPoliza;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    private final PolizaService polizaService;

    public PolizaController(PolizaService polizaService) {
        this.polizaService = polizaService;
    }

    @GetMapping
    public ResponseEntity<List<PolizaDTO>> listarPolizas(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        
        List<Poliza> polizas = polizaService.listarPolizas(tipo, estado);
        List<PolizaDTO> dtos = polizas.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @PostMapping
    public ResponseEntity<PolizaDTO> crearPoliza(@RequestBody PolizaDTO dto) {
        Poliza poliza = mapToEntity(dto);
        Poliza creada = polizaService.crearPoliza(poliza);
        return ResponseEntity.ok(mapToDTO(creada));
    }

    // 1. GET /polizas/{id}/riesgos
    @GetMapping("/{id}/riesgos")
    public ResponseEntity<List<Riesgo>> obtenerRiesgos(@PathVariable Long id) {
        List<Riesgo> riesgos = polizaService.obtenerRiesgosPorPoliza(id);
        return ResponseEntity.ok(riesgos);
    }

    // 3. POST /polizas/{id}/renovar
    @PostMapping("/{id}/renovar")
    public ResponseEntity<Poliza> renovarPoliza(
            @PathVariable Long id, 
            @RequestBody Map<String, BigDecimal> request) {
        
        BigDecimal ipc = request.get("ipc");
        if (ipc == null) {
            throw new IllegalArgumentException("IPC requerido");
        }
        
        Poliza polizaRenovada = polizaService.renovarPoliza(id, ipc);
        return ResponseEntity.ok(polizaRenovada);
    }

    // 5. POST /polizas/{id}/cancelar
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Poliza> cancelarPoliza(@PathVariable Long id) {
        Poliza polizaCancelada = polizaService.cancelarPoliza(id);
        return ResponseEntity.ok(polizaCancelada);
    }

    // 7. POST /polizas/{id}/riesgos
    @PostMapping("/{id}/riesgos")
    public ResponseEntity<Riesgo> agregarRiesgo(
            @PathVariable Long id, 
            @RequestBody Riesgo nuevoRiesgo) {
        
        Riesgo riesgoCreado = polizaService.agregarRiesgo(id, nuevoRiesgo);
        return ResponseEntity.status(HttpStatus.CREATED).body(riesgoCreado);
    }
    
    // Mappers simples
    private PolizaDTO mapToDTO(Poliza poliza) {
        PolizaDTO dto = new PolizaDTO();
        dto.setId(poliza.getId());
        dto.setTipo(poliza.getTipo());
        dto.setEstado(poliza.getEstado());
        dto.setFechaInicioVigencia(poliza.getFechaInicioVigencia());
        dto.setFechaFinVigencia(poliza.getFechaFinVigencia());
        dto.setCanonMensual(poliza.getCanonMensual());
        dto.setPrima(poliza.getPrima());
        return dto;
    }
    
    private Poliza mapToEntity(PolizaDTO dto) {
        Poliza poliza = new Poliza();
        poliza.setId(dto.getId());
        poliza.setTipo(dto.getTipo());
        poliza.setEstado(dto.getEstado());
        poliza.setFechaInicioVigencia(dto.getFechaInicioVigencia());
        poliza.setFechaFinVigencia(dto.getFechaFinVigencia());
        poliza.setCanonMensual(dto.getCanonMensual());
        // Prima se calcula
        return poliza;
    }
}

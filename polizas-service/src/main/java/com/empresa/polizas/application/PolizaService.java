package com.empresa.polizas.application;

import com.empresa.polizas.domain.*;
import com.empresa.polizas.infrastructure.repository.jpa.PolizaRepository;
import com.empresa.polizas.infrastructure.repository.jpa.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class PolizaService {

    private final PolizaRepository polizaRepository;
    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;

    public PolizaService(PolizaRepository polizaRepository, RiesgoRepository riesgoRepository, CoreMockService coreMockService) {
        this.polizaRepository = polizaRepository;
        this.riesgoRepository = riesgoRepository;
        this.coreMockService = coreMockService;
    }

    public List<Poliza> listarPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        if (tipo != null && estado != null) {
            return polizaRepository.findByTipoAndEstado(tipo, estado);
        } else if (tipo != null) {
            return polizaRepository.findByTipo(tipo);
        } else if (estado != null) {
            return polizaRepository.findByEstado(estado);
        } else {
            return polizaRepository.findAll();
        }
    }

    public Poliza crearPoliza(Poliza poliza) {
        // Validación: Una póliza individual solo puede tener 1 riesgo
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL && poliza.getRiesgos().size() > 1) {
            throw new IllegalArgumentException("Una póliza individual solo puede tener 1 riesgo.");
        }
        poliza.calcularPrima();
        
        // Asignar la póliza a cada riesgo para mantener la relación bidireccional
        if (poliza.getRiesgos() != null) {
            poliza.getRiesgos().forEach(r -> r.setPoliza(poliza));
        }

        return polizaRepository.save(poliza);
    }

    // 2. GET /polizas/{id}/riesgos
    public List<Riesgo> obtenerRiesgosPorPoliza(Long polizaId) {
        return riesgoRepository.findByPolizaId(polizaId);
    }

    // 4. POST /polizas/{id}/renovar
    public Poliza renovarPoliza(Long id, BigDecimal ipc) {
        Poliza poliza = polizaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Póliza no encontrada"));
        
        // RB-002: No renovar CANCELADA
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new RuntimeException("No se puede renovar póliza cancelada");
        }
        
        // Calcular nuevo canon
        BigDecimal nuevoCanon = poliza.getCanonMensual()
            .multiply(BigDecimal.ONE.add(ipc.divide(BigDecimal.valueOf(100))));
        
        // Recalcular prima (mismos meses)
        long meses = ChronoUnit.MONTHS.between(
            poliza.getFechaInicioVigencia(), 
            poliza.getFechaFinVigencia());
            
        // Ajuste por si meses es 0
        if (meses <= 0) meses = 1;
        
        BigDecimal nuevaPrima = nuevoCanon.multiply(BigDecimal.valueOf(meses));
        
        // Mover fechas al siguiente periodo
        LocalDate nuevaInicio = poliza.getFechaFinVigencia().plusDays(1);
        LocalDate nuevaFin = nuevaInicio.plusMonths(meses);
        
        // Actualizar y guardar
        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setFechaInicioVigencia(nuevaInicio);
        poliza.setFechaFinVigencia(nuevaFin);
        poliza.setEstado(EstadoPoliza.RENOVADA);
        
        // Llamar CORE mock
        coreMockService.enviarEvento("ACTUALIZACION", id);
        
        return polizaRepository.save(poliza);
    }

    // 6. POST /polizas/{id}/cancelar
    public Poliza cancelarPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Póliza no encontrada"));
        
        // Cambiar estado póliza
        poliza.setEstado(EstadoPoliza.CANCELADA);
        
        // Cancelar todos los riesgos (RB-003)
        List<Riesgo> riesgos = riesgoRepository.findByPolizaId(id);
        riesgos.forEach(riesgo -> riesgo.setEstado(EstadoRiesgo.CANCELADO));
        riesgoRepository.saveAll(riesgos);
        
        // CORE mock
        coreMockService.enviarEvento("ACTUALIZACION", id);
        
        return polizaRepository.save(poliza);
    }

    // 8. POST /polizas/{id}/riesgos
    public Riesgo agregarRiesgo(Long polizaId, Riesgo nuevoRiesgo) {
        Poliza poliza = polizaRepository.findById(polizaId)
            .orElseThrow(() -> new RuntimeException("Póliza no encontrada"));
        
        // RB-004: Solo COLECTIVA
        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new RuntimeException("Solo pólizas colectivas pueden tener múltiples riesgos");
        }
        
        nuevoRiesgo.setPoliza(poliza);
        nuevoRiesgo.setEstado(EstadoRiesgo.ACTIVO);
        
        Riesgo riesgoGuardado = riesgoRepository.save(nuevoRiesgo);
        
        // CORE mock
        coreMockService.enviarEvento("ACTUALIZACION", polizaId);
        
        return riesgoGuardado;
    }

    // Removed methods that were replaced by new ones or moved to RiesgoService if any
    // obtenerRiesgos -> replaced by obtenerRiesgosPorPoliza
    // renovar -> replaced by renovarPoliza
    // cancelar -> replaced by cancelarPoliza
    // agregarRiesgo -> replaced by agregarRiesgo (same name, slightly different impl)
    // cancelarRiesgo -> moved to RiesgoService
}

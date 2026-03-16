package com.empresa.polizas.application;

import com.empresa.polizas.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PolizaService {

    public Poliza crearPoliza(Poliza poliza) {
        // Validación: Una póliza individual solo puede tener 1 riesgo
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL && poliza.getRiesgos().size() > 1) {
            throw new IllegalArgumentException("Una póliza individual solo puede tener 1 riesgo.");
        }
        poliza.calcularPrima();
        return poliza; // Simulado, falta repository
    }

    public void renovar(Long id, Double ipc) {
        // Simulado: buscar póliza
        Poliza poliza = new Poliza(); // Dummy
        poliza.setEstado(EstadoPoliza.VIGENTE); // Dummy

        // Validación: No se puede renovar una póliza cancelada
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new IllegalStateException("No se puede renovar una póliza cancelada.");
        }

        // Lógica de renovación
        poliza.setCanonMensual(poliza.getCanonMensual() * (1 + ipc));
        poliza.calcularPrima();
        poliza.setEstado(EstadoPoliza.RENOVADA);
    }

    public void cancelar(Long id) {
        // Simulado: buscar póliza
        Poliza poliza = new Poliza(); // Dummy
        
        // Regla: La cancelación de una póliza cancela todos sus riesgos
        poliza.setEstado(EstadoPoliza.CANCELADA);
        poliza.getRiesgos().forEach(r -> r.setEstado(EstadoRiesgo.CANCELADO));
    }

    public void agregarRiesgo(Long polizaId, Riesgo riesgo) {
        // Simulado: buscar póliza
        Poliza poliza = new Poliza(); // Dummy
        poliza.setTipo(TipoPoliza.INDIVIDUAL); // Dummy para probar validación

        // Validación: Agregar riesgo exige validación del tipo de póliza (solo Colectiva)
        // Nota: El enunciado dice "Agregar riesgo exige validación del tipo de póliza".
        // Si es INDIVIDUAL y ya tiene 1, no deja. Si es COLECTIVA, deja.
        // Asumimos que si es INDIVIDUAL, no se pueden agregar más si ya existe uno.
        
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL && !poliza.getRiesgos().isEmpty()) {
             throw new IllegalArgumentException("No se pueden agregar más riesgos a una póliza individual.");
        }
        
        // O una interpretación más estricta del enunciado "Agregar riesgo solo está permitido cuando tipo = COLECTIVA" 
        // para el endpoint POST /polizas/{id}/riesgos
        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new IllegalArgumentException("Solo se pueden agregar riesgos a pólizas colectivas.");
        }

        riesgo.setPoliza(poliza);
        poliza.getRiesgos().add(riesgo);
    }
}

package com.empresa.polizas.application;

import com.empresa.polizas.domain.EstadoRiesgo;
import com.empresa.polizas.domain.Riesgo;
import com.empresa.polizas.infrastructure.repository.jpa.RiesgoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final CoreMockService coreMockService;

    public RiesgoService(RiesgoRepository riesgoRepository, CoreMockService coreMockService) {
        this.riesgoRepository = riesgoRepository;
        this.coreMockService = coreMockService;
    }

    public Riesgo cancelarRiesgo(Long id) {
        Riesgo riesgo = riesgoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Riesgo no encontrado"));

        riesgo.setEstado(EstadoRiesgo.CANCELADO);

        // CORE mock para la póliza padre
        coreMockService.enviarEvento("ACTUALIZACION", riesgo.getPoliza().getId());

        return riesgoRepository.save(riesgo);
    }
}

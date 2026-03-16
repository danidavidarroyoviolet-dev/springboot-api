package com.empresa.polizas.infrastructure.repository.jpa;

import com.empresa.polizas.domain.EstadoPoliza;
import com.empresa.polizas.domain.Poliza;
import com.empresa.polizas.domain.TipoPoliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {
    List<Poliza> findByTipo(TipoPoliza tipo);
    List<Poliza> findByEstado(EstadoPoliza estado);
    List<Poliza> findByTipoAndEstado(TipoPoliza tipo, EstadoPoliza estado);
}

package com.empresa.polizas.infrastructure.repository.jpa;

import com.empresa.polizas.domain.Riesgo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiesgoRepository extends JpaRepository<Riesgo, Long> {
    List<Riesgo> findByPolizaId(Long polizaId);
}

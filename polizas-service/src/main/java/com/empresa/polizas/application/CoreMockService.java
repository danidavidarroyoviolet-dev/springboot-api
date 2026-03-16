package com.empresa.polizas.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CoreMockService {

    private static final Logger logger = LoggerFactory.getLogger(CoreMockService.class);

    public void enviarEvento(String tipoEvento, Long recursoId) {
        logger.info("Enviando evento al CORE Mock: Tipo={}, RecursoID={}", tipoEvento, recursoId);
        // Aquí se podría implementar una llamada HTTP real si el Mock fuera un servicio externo
    }
}

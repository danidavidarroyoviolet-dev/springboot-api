package com.empresa.polizas.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger logger = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Void> recibirEvento(@RequestBody CoreEventoRequest request) {
        logger.info("MOCK CORE: Recibido evento {} para polizaId {}", request.getEvento(), request.getPolizaId());
        return ResponseEntity.ok().build();
    }
}

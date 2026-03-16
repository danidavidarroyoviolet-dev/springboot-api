package com.empresa.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/polizas")
    public Mono<String> polizasFallback() {
        return Mono.just("El servicio de Pólizas no está disponible temporalmente. Por favor, intente más tarde.");
    }
}

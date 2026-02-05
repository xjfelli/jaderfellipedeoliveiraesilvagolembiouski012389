package com.gerenciadorartistas.backend.core.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health", description = "Verificação de saúde da API")
public class HealthController {

    @GetMapping("/health")
    @Operation(
        summary = "Verificar saúde da API",
        description = "Retorna o status da API e timestamp atual"
    )
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put(
            "message",
            "API Gerenciador de Artistas está funcionando!"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    @Operation(
        summary = "Informações da API",
        description = "Retorna informações sobre a API"
    )
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "API Gerenciador de Artistas");
        response.put("version", "1.0.0");
        response.put(
            "description",
            "API REST para gerenciamento de artistas e álbuns musicais"
        );
        response.put(
            "endpoints",
            Map.of(
                "artistas",
                "/api/artistas",
                "albums",
                "/api/albums",
                "swagger",
                "/swagger-ui.html"
            )
        );
        return ResponseEntity.ok(response);
    }
}

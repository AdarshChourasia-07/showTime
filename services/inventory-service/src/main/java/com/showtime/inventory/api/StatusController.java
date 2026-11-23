package com.showtime.inventory.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Placeholder so the gateway route can be tested. Replaced by real endpoints in Phase 1. */
@RestController
public class StatusController {

    @GetMapping("/api/inventory/status")
    public Map<String, String> status() {
        return Map.of("service", "inventory-service", "status", "skeleton - implemented in Phase 1");
    }
}

package org.derleta.authorization.controller;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    private final String appVersion;

    public HealthController(@Value("${app.version:unknown}") String appVersion) {
        this.appVersion = appVersion;
    }

    @Timed(
            value = "http.api.v1.health.hello",
            description = "Time spent handling GET /api/v1/hello",
            percentiles = {0.5, 0.95, 0.99}
    )
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello");
    }

    @Timed(
            value = "http.api.v1.health.version",
            description = "Time spent handling GET /api/v1/version",
            percentiles = {0.5, 0.95, 0.99}
    )
    @GetMapping("/version")
    public ResponseEntity<String> version() {
        return ResponseEntity.ok(appVersion);
    }

}

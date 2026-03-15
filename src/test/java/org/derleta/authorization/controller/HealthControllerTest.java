package org.derleta.authorization.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HealthControllerTest {

    @Test
    void testHello() {
        HealthController controller = new HealthController("1.0.0");
        ResponseEntity<String> response = controller.hello();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("hello", response.getBody());
    }

    @Test
    void testVersion() {
        HealthController controller = new HealthController("1.0.0");
        ResponseEntity<String> response = controller.version();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("1.0.0", response.getBody());
    }
}

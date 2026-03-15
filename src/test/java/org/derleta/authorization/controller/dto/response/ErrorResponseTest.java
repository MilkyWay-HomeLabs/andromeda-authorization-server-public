package org.derleta.authorization.controller.dto.response;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseRecord() {
        int status = 404;
        String message = "Not Found";
        LocalDateTime timestamp = LocalDateTime.now();

        ErrorResponse response = new ErrorResponse(status, message, timestamp);

        assertEquals(status, response.status());
        assertEquals(message, response.message());
        assertEquals(timestamp, response.timestamp());
    }
}

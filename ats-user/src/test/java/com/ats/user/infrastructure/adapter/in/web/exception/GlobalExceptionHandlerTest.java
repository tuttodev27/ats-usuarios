package com.ats.user.infrastructure.adapter.in.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private HttpServletRequest request;

    @Test
    void handleGenericShouldIncludeErrorId() {
        when(request.getRequestURI()).thenReturn("/api/test");

        var response = handler.handleGeneric(new RuntimeException("test error"), request);

        assertEquals(500, response.getStatusCode().value());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals("INTERNAL_ERROR", body.code());
        assertEquals("Unexpected error", body.message());
        assertNotNull(body.errorId());
        assertEquals(36, body.errorId().length());
    }
}

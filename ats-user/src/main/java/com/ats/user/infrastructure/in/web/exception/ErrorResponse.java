package com.ats.user.infrastructure.in.web.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(example = """
        {
          "timestamp": "2026-06-05T10:00:00Z",
          "status": 401,
          "code": "UNAUTHORIZED",
          "message": "Authentication required",
          "path": "/api/users"
        }
        """)
public record ErrorResponse(
        @Schema(example = "2026-06-05T10:00:00Z") Instant timestamp,
        @Schema(example = "401") int status,
        @Schema(example = "UNAUTHORIZED") String code,
        @Schema(example = "Authentication required") String message,
        @Schema(example = "/api/users") String path
) {
}

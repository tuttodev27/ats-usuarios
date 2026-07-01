package com.ats.user.infrastructure.adapter.in.web.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(example = """
        {
          "timestamp": "2026-06-05T10:00:00Z",
          "status": 500,
          "code": "INTERNAL_ERROR",
          "message": "Unexpected error",
          "path": "/api/users",
          "errorId": "a1b2c3d4-1234-5678-9abc-def012345678"
        }
        """)
public record ErrorResponse(
        @Schema(example = "2026-06-05T10:00:00Z") Instant timestamp,
        @Schema(example = "500") int status,
        @Schema(example = "INTERNAL_ERROR") String code,
        @Schema(example = "Unexpected error") String message,
        @Schema(example = "/api/users") String path,
        @Schema(example = "a1b2c3d4-1234-5678-9abc-def012345678") String errorId
) {

    public ErrorResponse(Instant timestamp, int status, String code, String message, String path) {
        this(timestamp, status, code, message, path, null);
    }
}

package com.ats.user.infrastructure.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(example = """
        {
          "code": "ATS",
          "name": "Applicant Tracking System",
          "description": "Modulo ATS actualizado"
        }
        """)
public record UpdateModuleRequest(
        @NotBlank
        @Size(max = 60)
        @Schema(example = "ATS")
        String code,

        @NotBlank
        @Size(max = 100)
        @Schema(example = "Applicant Tracking System")
        String name,

        @Size(max = 255)
        @Schema(example = "Modulo ATS actualizado")
        String description
) {
}

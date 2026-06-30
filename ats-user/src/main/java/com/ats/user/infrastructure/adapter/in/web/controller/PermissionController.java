package com.ats.user.infrastructure.adapter.in.web.controller;

import com.ats.user.domain.port.in.PermissionUseCase;
import com.ats.user.infrastructure.adapter.in.web.dto.response.PermissionResponse;
import com.ats.user.infrastructure.adapter.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.adapter.in.web.mapper.PermissionWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Catalogo de permisos del sistema")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private final PermissionUseCase permissionUseCase;
    private final PermissionWebMapper permissionWebMapper;

    @GetMapping
    @Operation(summary = "Listar permisos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catalogo de permisos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PermissionResponse.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<PermissionResponse>> list(
            @Parameter(description = "Filtrar por ID de modulo")
            @RequestParam(required = false) Long moduleId,
            @Parameter(description = "Filtrar por estado activo")
            @RequestParam(required = false) Boolean active) {
        var permissions = permissionUseCase.list(moduleId, active).stream()
                .map(permissionWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(permissions);
    }
}

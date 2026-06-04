package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.port.in.ModuleUserCase;
import com.ats.user.infrastructure.in.web.dto.request.CreateModuleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateModuleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateModuleStatusRequest;
import com.ats.user.infrastructure.in.web.dto.response.ModuleResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.in.web.mapper.ModuleWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules", description = "Gestion de modulos del sistema")
@SecurityRequirement(name = "bearerAuth")
public class ModuleController {

    private final ModuleUserCase moduleUseCase;
    private final ModuleWebMapper moduleWebMapper;

    @PostMapping
    @Operation(summary = "Crear modulo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Modulo creado",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Modulo duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ModuleResponse> create(@Valid @RequestBody CreateModuleRequest request) {
        var created = moduleUseCase.createModule(moduleWebMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Listar modulos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de modulos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ModuleResponse.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ModuleResponse>> list() {
        var modules = moduleUseCase.getModules().stream()
                .map(moduleWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener modulo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modulo encontrado",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ModuleResponse> getById(@Parameter(example = "1") @PathVariable Long id) {
        var module = moduleUseCase.getModuleById(id);
        return ResponseEntity.ok(moduleWebMapper.toResponse(module));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar modulo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Modulo actualizado",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Modulo duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ModuleResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateModuleRequest request) {
        var updated = moduleUseCase.updateModule(id, moduleWebMapper.toDomain(request));
        return ResponseEntity.ok(moduleWebMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar modulo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del modulo actualizado",
                    content = @Content(schema = @Schema(implementation = ModuleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ModuleResponse> updateStatus(
            @Parameter(example = "1") @PathVariable Long id,
            @Valid @RequestBody UpdateModuleStatusRequest request) {
        var updated = moduleUseCase.updateModuleStatus(id, request.active());
        return ResponseEntity.ok(moduleWebMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar modulo (borrado logico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Modulo eliminado logicamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        moduleUseCase.deleteModuleById(id);
        return ResponseEntity.noContent().build();
    }
}

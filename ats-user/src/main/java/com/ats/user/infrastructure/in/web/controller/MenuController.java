package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.port.in.MenuUseCase;
import com.ats.user.infrastructure.in.web.dto.request.CreateMenuRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateMenuRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateMenuStatusRequest;
import com.ats.user.infrastructure.in.web.dto.response.MenuResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.in.web.mapper.MenuWebMapper;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Menus", description = "Gestion de menus de navegacion")
@SecurityRequirement(name = "bearerAuth")
public class MenuController {

    private final MenuUseCase menuUseCase;
    private final MenuWebMapper menuWebMapper;

    @PostMapping
    @Operation(summary = "Crear menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Menu creado",
                    content = @Content(schema = @Schema(implementation = MenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Menu duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MenuResponse> create(@Valid @RequestBody CreateMenuRequest request) {
        var created = menuUseCase.create(menuWebMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(menuWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Listar menus")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de menus",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MenuResponse.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<MenuResponse>> list() {
        var menus = menuUseCase.list().stream()
                .map(menuWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener menu por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu encontrado",
                    content = @Content(schema = @Schema(implementation = MenuResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MenuResponse> getById(@PathVariable Long id) {
        var menu = menuUseCase.getById(id);
        return ResponseEntity.ok(menuWebMapper.toResponse(menu));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Menu actualizado",
                    content = @Content(schema = @Schema(implementation = MenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu o modulo no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Menu duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MenuResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateMenuRequest request) {
        var updated = menuUseCase.update(id, menuWebMapper.toDomain(request));
        return ResponseEntity.ok(menuWebMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar menu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del menu actualizado",
                    content = @Content(schema = @Schema(implementation = MenuResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MenuResponse> updateStatus(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateMenuStatusRequest request) {
        var updated = menuUseCase.updateStatus(id, request.active());
        return ResponseEntity.ok(menuWebMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar menu (borrado logico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Menu eliminado logicamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Menu no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}

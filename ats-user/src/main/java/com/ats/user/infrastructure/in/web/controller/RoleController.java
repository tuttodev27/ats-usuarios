package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.infrastructure.in.web.dto.request.AssignPermissionRequest;
import com.ats.user.infrastructure.in.web.dto.request.CreateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateRoleStatusRequest;
import com.ats.user.infrastructure.in.web.dto.response.PagedResponse;
import com.ats.user.infrastructure.in.web.dto.response.RoleDetailResponse;
import com.ats.user.infrastructure.in.web.dto.response.RolePermissionsResponse;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.in.web.mapper.RoleWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestion de roles")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final RoleWebMapper roleWebMapper;

    @PostMapping
    @Operation(
            summary = "Crear rol",
            description = "Crea un nuevo rol en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rol creado",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nombre de rol ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        var created = roleUseCase.create(roleWebMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(roleWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(
            summary = "Listar roles con busqueda, filtro y paginacion",
            description = "Obtiene todos los roles registrados con soporte de filtros y paginacion."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado paginado de roles",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<RoleResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageQuery = new PageQuery(page, size);
        var result = roleUseCase.listRoles(search, active, pageQuery);
        var content = result.getContent().stream()
                .map(roleWebMapper::toResponse)
                .toList();
        var response = new PagedResponse<>(content, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener rol por ID",
            description = "Retorna un rol especifico por su identificador con sus permisos asociados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado",
                    content = @Content(schema = @Schema(implementation = RoleDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleDetailResponse> getById(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id
    ) {
        var role = roleUseCase.getById(id);
        return ResponseEntity.ok(roleWebMapper.toDetailResponse(role));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar rol",
            description = "Actualiza nombre y descripcion del rol."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Nombre de rol ya existe",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleResponse> update(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload para actualizar rol",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateRoleRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "ADMIN",
                                      "description": "Rol administrador"
                                    }
                                    """)
                    )
            )
            @Valid @RequestBody UpdateRoleRequest request
    ) {
        var updated = roleUseCase.update(id, roleWebMapper.toDomain(request));
        return ResponseEntity.ok(roleWebMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar rol (borrado logico)",
            description = "Desactiva el rol estableciendo active = false."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado logicamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id
    ) {
        roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/permissions")
    @Operation(
            summary = "Asignar permisos a rol",
            description = "Reemplaza la asignacion actual de permisos del rol con la lista enviada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permisos asignados",
                    content = @Content(schema = @Schema(implementation = RolePermissionsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol o permisos no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RolePermissionsResponse> assignPermissions(
            @Parameter(description = "ID del rol", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lista de IDs de permisos que se asignaran al rol",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AssignPermissionRequest.class),
                            examples = @ExampleObject(
                                    name = "Asignar permisos",
                                    value = """
                                            {
                                              "permissionIds": [1, 2, 3]
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        var updatedRole = roleUseCase.assignPermissions(id, request.permissionIds());
        return ResponseEntity.ok(roleWebMapper.toPermissionsResponse(updatedRole));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar o desactivar rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado del rol actualizado",
                    content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RoleResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleStatusRequest request){
        var updated = roleUseCase.updateStatus(id, request.active());
        return ResponseEntity.ok(roleWebMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}/permissions")
    @Operation(
            summary = "Quitar permisos de rol con borrado logico",
            description = "Desactiva la relacion entre el rol y los permisos enviados."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permisos desasignados logicamente",
                    content = @Content(schema = @Schema(implementation = RolePermissionsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Rol o permisos no encontrados",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RolePermissionsResponse> deletePermissions(
            @PathVariable Long id,
            @Valid @RequestBody AssignPermissionRequest request
    ) {
        var updatedRole = roleUseCase.deletePermissions(id, request.permissionIds());
        return ResponseEntity.ok(roleWebMapper.toPermissionsResponse(updatedRole));
    }

}

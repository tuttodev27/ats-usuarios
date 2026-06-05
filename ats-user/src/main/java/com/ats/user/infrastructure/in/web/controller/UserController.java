package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserStatusRequest;
import com.ats.user.infrastructure.in.web.dto.request.UserRequest;
import com.ats.user.infrastructure.in.web.dto.response.PagedResponse;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.in.web.mapper.RoleWebMapper;
import com.ats.user.infrastructure.in.web.mapper.UserWebMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion de usuarios")
@SecurityRequirement(name = "bearerAuth")

public class UserController {
    private final UserUseCase userUseCase;
    private final UserWebMapper userWebMapper;
    private final RoleWebMapper roleWebMapper;

    @PostMapping()
    @Operation(summary = "Crear usuario", description = "Crea un usuario nuevo y asigna un rol por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Email duplicado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest request) {
        var user= userWebMapper.toDomain(request);
        var created= userUseCase.create(user, request.roleId(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(userWebMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userUseCase.getById(id);
        return ResponseEntity.ok(userWebMapper.toResponse(user));
    }
    @GetMapping
    @Operation(summary = "Listar usuarios con busqueda, filtro y paginacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de usuarios paginado",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<UserResponse>> listUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageQuery = new PageQuery(page, size);
        var result = userUseCase.listUsers(search, active, pageQuery);
        var content = result.getContent().stream()
                .map(userWebMapper::toResponse)
                .toList();
        var response = new PagedResponse<>(content, result.getPage(), result.getSize(), result.getTotalElements(), result.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles activos disponibles para asignacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles activos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponse.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<RoleResponse>> listAvailableRoles() {
        var roles = userUseCase.listAvailableRoles().stream()
                .map(roleWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (borrado logico)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado logicamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        var user = userWebMapper.toDomain(request);
        var updated = userUseCase.update(id, user);
        return ResponseEntity.ok(userWebMapper.toResponse(updated));
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Cambiar rol de un usuario", description = "Reemplaza el rol actual del usuario por uno nuevo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol asignado correctamente",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        var updated = userUseCase.changeUserRole(id, request.roleId());
        return ResponseEntity.ok(userWebMapper.toResponse(updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activar/desactivar usuario", description = "Cambia el estado activo/inactivo del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud invalida",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sin permisos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request
    ) {
        var updated = userUseCase.updateStatus(id, request.active());
        return ResponseEntity.ok(userWebMapper.toResponse(updated));
    }
}

package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRequest;
import com.ats.user.infrastructure.in.web.dto.request.UserRequest;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import com.ats.user.infrastructure.in.web.mapper.RoleWebMapper;
import com.ats.user.infrastructure.in.web.mapper.UserWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Operation(summary = "Crear usuario", description = "Crea un usuario nuevo y asigna un rol")
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest request) {
        var user= userWebMapper.toDomain(request);
        var created= userUseCase.create(user, request.role(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(userWebMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userUseCase.getById(id);
        return ResponseEntity.ok(userWebMapper.toResponse(user));
    }
    @GetMapping
    @Operation(summary = "Listar usuarios")
    public ResponseEntity<List<UserResponse>> listUsers(@RequestParam(required = false) Boolean active) {
        List<UserResponse> responses = userUseCase.listUsers(active)
                .stream()
                .map(userWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles activos disponibles para asignacion")
    public ResponseEntity<List<RoleResponse>> listAvailableRoles() {
        var roles = userUseCase.listAvailableRoles().stream()
                .map(roleWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario (borrado logico)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        var user = userWebMapper.toDomain(request);
        var updated = userUseCase.update(id, user);
        return ResponseEntity.ok(userWebMapper.toResponse(updated));
    }
}

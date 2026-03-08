package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.infrastructure.in.web.dto.request.CreateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.request.UpdateRoleRequest;
import com.ats.user.infrastructure.in.web.dto.response.RoleResponse;
import com.ats.user.infrastructure.in.web.mapper.RoleWebMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Gestion de roles")
@SecurityRequirement(name = "bearerAuth")
public class RoleController {

    private final RoleUseCase roleUseCase;
    private final RoleWebMapper roleWebMapper;

    @PostMapping
    @Operation(summary = "Crear rol")
    public ResponseEntity<RoleResponse> create(@Valid @RequestBody CreateRoleRequest request) {
        var created = roleUseCase.create(roleWebMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(roleWebMapper.toResponse(created));
    }

    @GetMapping
    @Operation(summary = "Listar roles")
    public ResponseEntity<List<RoleResponse>> list() {
        var roles = roleUseCase.list().stream()
                .map(roleWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
        var role = roleUseCase.getById(id);
        return ResponseEntity.ok(roleWebMapper.toResponse(role));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar rol")
    public ResponseEntity<RoleResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody UpdateRoleRequest request) {
        var updated = roleUseCase.update(id, roleWebMapper.toDomain(request));
        return ResponseEntity.ok(roleWebMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar rol (borrado logico)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }
}

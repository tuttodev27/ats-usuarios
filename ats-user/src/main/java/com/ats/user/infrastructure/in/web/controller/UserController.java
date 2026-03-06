package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.exception.InvalidPasswordException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.infrastructure.in.web.dto.request.UpdateUserRequest;
import com.ats.user.infrastructure.in.web.dto.request.UserRequest;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import com.ats.user.infrastructure.in.web.mapper.UserWebMapper;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestion de usuarios")
@SecurityRequirement(name = "bearerAuth")

public class UserController {
    private final UserUseCase userUseCase;
    private final UserWebMapper userWebMapper;
    private final RoleJpaRepository roleJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping()
    @Operation(summary = "Crear usuario", description = "Crea un usuario nuevo y asigna un rol")
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody UserRequest request) {
        validatePassword(request.password());
        var user= userWebMapper.toDomain(request);
        user.setRoles(Set.of(Role.builder()
                .name(request.role().trim().toUpperCase())
                .build()));

        user.setPasswordHash(passwordEncoder.encode(request.password()));

        var created= userUseCase.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userWebMapper.toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userUseCase.getById(id);
        return ResponseEntity.ok(userWebMapper.toResponse(user));
    }
    @GetMapping
    @Operation(summary = "Listar usuarios activos")
    public ResponseEntity<List<UserResponse>> listActive() {
        List<UserResponse> responses = userUseCase.listActive()
                .stream()
                .map(userWebMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/roles")
    @Operation(summary = "Listar roles disponibles")
    public ResponseEntity<List<String>> listAvailableRoles() {
        List<String> roles = roleJpaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(role -> role.getName())
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

    private void validatePassword(String rawPassword) {
        boolean hasLetter = rawPassword.chars().anyMatch(Character::isLetter);
        boolean hasDigit = rawPassword.chars().anyMatch(Character::isDigit);
        if (!hasLetter || !hasDigit) {
            throw new InvalidPasswordException("Password must include at least one letter and one number");
        }
    }
}

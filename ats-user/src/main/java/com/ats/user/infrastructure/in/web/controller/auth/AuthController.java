package com.ats.user.infrastructure.in.web.controller.auth;

import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.infrastructure.in.web.dto.request.LoginRequest;
import com.ats.user.infrastructure.in.web.dto.response.LoginResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import com.ats.user.infrastructure.in.web.security.jwt.JwtService;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.RolePermissionEntity;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticacion y emision de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserJpaRepository userRepository;

    @Value("${security.jwt.expiration-minutes:50}")
    private long expirationMinutes;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserDetailsService userDetailsService,
                          UserJpaRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Autentica usuario por email/password y devuelve token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticacion exitosa, retorna JWT",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales invalidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.email()));

        var activeRoles = user.getRoles().stream()
                .filter(RoleEntity::getActive)
                .toList();

        if (activeRoles.isEmpty()) {
            throw new AccessDeniedException("User has no active roles");
        }

        var userDetails = userDetailsService.loadUserByUsername(request.email());

        List<String> roleNames = activeRoles.stream()
                .map(RoleEntity::getName)
                .map(name -> name.toUpperCase().startsWith("ROLE_") ? name.toUpperCase() : "ROLE_" + name.toUpperCase())
                .distinct()
                .toList();

        List<String> permissions = activeRoles.stream()
                .flatMap(role -> role.getRolePermissions().stream())
                .filter(rp -> Boolean.TRUE.equals(rp.getActive()))
                .map(rp -> rp.getPermission().getCode())
                .distinct()
                .toList();

        Map<String, Object> extraClaims = Map.of(
                "userId", user.getId(),
                "roles", roleNames,
                "permissions", permissions,
                "email", user.getEmail()
        );

        String token = jwtService.generateToken(user.getEmail(), extraClaims);
        long expiresInSeconds = expirationMinutes * 60L;

        var userInfo = new LoginResponse.UserInfo(
                user.getId(), user.getName(), user.getLastName(), user.getEmail(), user.getPhone()
        );

        return new LoginResponse(token, "Bearer", expiresInSeconds, userInfo, roleNames, permissions);
    }
}

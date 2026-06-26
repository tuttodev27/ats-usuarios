package com.ats.user.infrastructure.in.web.controller.auth;

import com.ats.user.domain.model.AuthResult;
import com.ats.user.domain.port.in.AuthUseCase;
import com.ats.user.domain.port.out.TokenPort;
import com.ats.user.infrastructure.in.web.dto.request.LoginRequest;
import com.ats.user.infrastructure.in.web.dto.response.LoginResponse;
import com.ats.user.infrastructure.in.web.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticacion y emision de token JWT")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AuthUseCase authUseCase;
    private final TokenPort tokenPort;

    @Value("${security.jwt.expiration-minutes:50}")
    private long expirationMinutes;

    public AuthController(AuthenticationManager authenticationManager,
                          AuthUseCase authUseCase,
                          TokenPort tokenPort) {
        this.authenticationManager = authenticationManager;
        this.authUseCase = authUseCase;
        this.tokenPort = tokenPort;
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

        AuthResult result = authUseCase.login(request.email());

        Map<String, Object> extraClaims = Map.of(
                "userId", result.userId(),
                "roles", result.roles(),
                "permissions", result.permissions(),
                "email", result.email()
        );

        String token = tokenPort.generateToken(result.email(), extraClaims);
        long expiresInSeconds = expirationMinutes * 60L;

        var userInfo = new LoginResponse.UserInfo(
                result.userId(), result.name(), result.lastName(), result.email(), result.phone()
        );

        return new LoginResponse(token, "Bearer", expiresInSeconds, userInfo, result.roles(), result.permissions());
    }
}

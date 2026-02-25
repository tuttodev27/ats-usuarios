package com.ats.user.infrastructure.in.web.controller;

import com.ats.user.domain.port.in.CreateUserUseCase;
import com.ats.user.infrastructure.in.web.dto.request.CreatedUserRequest;
import com.ats.user.infrastructure.in.web.dto.response.UserResponse;
import com.ats.user.infrastructure.in.web.mapper.UserWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor

public class UserController {
    private final CreateUserUseCase createUserUseCase;
    private final UserWebMapper userWebMapper;;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping()
    public ResponseEntity<UserResponse> saveUser(@Valid @RequestBody CreatedUserRequest request) {
        var user= userWebMapper.toDomain(request);

        user.setPasswordHash(passwordEncoder.encode(request.password()));

        var created= createUserUseCase.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userWebMapper.toResponse(created));
    }
}

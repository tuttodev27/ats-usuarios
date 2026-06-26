package com.ats.user.application.service;

import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.AuthResult;
import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.AuthUseCase;
import com.ats.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthUseCaseImpl implements AuthUseCase {

    private final UserRepositoryPort userRepository;

    public AuthUseCaseImpl(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AuthResult login(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));

        var activeRoles = user.getRoles().stream()
                .filter(Role::isActive)
                .toList();

        if (activeRoles.isEmpty()) {
            throw new com.ats.user.domain.exception.RoleNotAvailableException("User has no active roles");
        }

        List<String> roleNames = activeRoles.stream()
                .map(Role::getName)
                .map(name -> name.toUpperCase().startsWith("ROLE_") ? name.toUpperCase() : "ROLE_" + name.toUpperCase())
                .distinct()
                .toList();

        List<String> permissions = activeRoles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .filter(Permission::isActive)
                .map(Permission::getCode)
                .distinct()
                .toList();

        return new AuthResult(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                roleNames,
                permissions
        );
    }
}

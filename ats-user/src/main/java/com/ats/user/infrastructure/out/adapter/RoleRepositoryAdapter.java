package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = role.getId() != null
                ? roleJpaRepository.findWithPermissionsById(role.getId()).orElse(new RoleEntity())
                : new RoleEntity();

        entity.setName(role.getName());
        entity.setDescription(role.getDescription());
        entity.setActive(role.isActive());
        entity.setCreatedAt(role.getCreatedAt());
        entity.setUpdatedAt(role.getUpdatedAt());

        Set<Long> permissionIds = role.getPermissions().stream()
                .map(Permission::getId)
                .filter(id -> id != null)
                .collect(java.util.stream.Collectors.toSet());

        if (!permissionIds.isEmpty()) {
            entity.setPermissions(new HashSet<>(permissionJpaRepository.findAllById(permissionIds)));
        } else {
            entity.setPermissions(new HashSet<>());
        }

        var saved = roleJpaRepository.save(entity);
        var reloaded = roleJpaRepository.findWithPermissionsById(saved.getId()).orElse(saved);
        return toDomain(reloaded);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findWithPermissionsById(id).map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String roleName) {
        return roleJpaRepository.existsByNameIgnoreCase(roleName);
    }

    @Override
    public Optional<Role> findByNameIgnoreCase(String roleName) {
        return roleJpaRepository.findByNameIgnoreCase(roleName).map(this::toDomain);
    }

    @Override
    public Optional<Role> findActiveByName(String roleName) {
        return roleJpaRepository.findByNameIgnoreCaseAndActiveTrue(roleName).map(this::toDomain);
    }

    @Override
    public List<String> listActiveRoleNames() {
        return roleJpaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(role -> role.getName())
                .toList();
    }

    private Role toDomain(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions().stream()
                .map(p -> Permission.builder()
                        .id(p.getId())
                        .code(p.getCode())
                        .resource(p.getResource())
                        .action(p.getAction())
                        .scope(p.getScope())
                        .description(p.getDescription())
                        .moduleId(p.getModule() != null ? p.getModule().getId() : null)
                        .active(Boolean.TRUE.equals(p.getActive()))
                        .createdAt(p.getCreatedAt())
                        .updatedAt(p.getUpdatedAt())
                        .createdBy(p.getCreatedBy())
                        .updatedBy(p.getUpdatedBy())
                        .build())
                .collect(java.util.stream.Collectors.toSet());

        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .permissions(permissions)
                .build();
    }
}

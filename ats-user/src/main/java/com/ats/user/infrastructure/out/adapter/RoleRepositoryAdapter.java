package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Role save(Role role) {
        var entity = roleJpaRepository.save(com.ats.user.infrastructure.out.entity.RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .active(role.isActive())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build());
        return toDomain(entity);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
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

    private Role toDomain(com.ats.user.infrastructure.out.entity.RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

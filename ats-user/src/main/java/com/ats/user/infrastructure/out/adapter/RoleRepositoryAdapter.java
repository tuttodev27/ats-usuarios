package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.mapper.RoleMapper;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;
    private final RolePermissionSyncSupport rolePermissionSyncSupport;
    private final RoleMapper roleMapper;

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

        if (entity.getId() == null) {
            entity = roleJpaRepository.save(entity);
            entity = roleJpaRepository.findWithPermissionsById(entity.getId()).orElse(entity);
        }

        rolePermissionSyncSupport.sync(entity, role.getPermissions());

        var saved = roleJpaRepository.save(entity);
        var reloaded = roleJpaRepository.findWithPermissionsById(saved.getId()).orElse(saved);
        return roleMapper.toDomain(reloaded);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleJpaRepository.findWithPermissionsById(id).map(roleMapper::toDomain);
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAllByOrderByIdAsc().stream()
                .map(roleMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByNameIgnoreCase(String roleName) {
        return roleJpaRepository.existsByNameIgnoreCase(roleName);
    }

    @Override
    public Optional<Role> findByNameIgnoreCase(String roleName) {
        return roleJpaRepository.findByNameIgnoreCase(roleName).map(roleMapper::toDomain);
    }

    @Override
    public Optional<Role> findActiveByName(String roleName) {
        return roleJpaRepository.findByNameIgnoreCaseAndActiveTrue(roleName).map(roleMapper::toDomain);
    }

    @Override
    public List<String> listActiveRoleNames() {
        return roleJpaRepository.findAllByActiveTrueOrderByNameAsc().stream()
                .map(RoleEntity::getName)
                .toList();
    }

}

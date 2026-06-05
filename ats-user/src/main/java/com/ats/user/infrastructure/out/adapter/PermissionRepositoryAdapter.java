package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import com.ats.user.infrastructure.out.entity.PermissionEntity;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public List<Permission> findAllByIds(List<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAll(Long moduleId, Boolean active) {
        return permissionJpaRepository.findAllFiltered(moduleId, active).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAllWithModuleOrderByModuleCodeAscCodeAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(this::toDomain);
    }

    private Permission toDomain(PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .resource(entity.getResource())
                .action(entity.getAction())
                .scope(entity.getScope())
                .description(entity.getDescription())
                .moduleId(entity.getModule() != null ? entity.getModule().getId() : null)
                .moduleCode(entity.getModule() != null ? entity.getModule().getCode() : null)
                .moduleName(entity.getModule() != null ? entity.getModule().getName() : null)
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}

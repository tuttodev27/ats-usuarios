package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<Permission> findAll() {
        return permissionJpaRepository.findAllWithModuleOrderByModuleCodeAscCodeAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    private Permission toDomain(com.ats.user.infrastructure.out.entity.PermissionEntity entity) {
        return Permission.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .resource(entity.getResource())
                .action(entity.getAction())
                .scope(entity.getScope())
                .description(entity.getDescription())
                .moduleId(entity.getModule() != null ? entity.getModule().getId() : null)
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}

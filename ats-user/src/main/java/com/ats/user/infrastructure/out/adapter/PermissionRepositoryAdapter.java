package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import com.ats.user.infrastructure.out.mapper.PermissionMapper;
import com.ats.user.infrastructure.out.repository.PermissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAllByIds(List<Long> ids) {
        return permissionJpaRepository.findAllById(ids).stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAll(Long moduleId, Boolean active) {
        return permissionJpaRepository.findAllFiltered(moduleId, active).stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAllWithModuleOrderByModuleCodeAscCodeAsc().stream()
                .map(permissionMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJpaRepository.findById(id).map(permissionMapper::toDomain);
    }
}

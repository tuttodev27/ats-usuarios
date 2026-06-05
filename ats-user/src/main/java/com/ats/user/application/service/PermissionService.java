package com.ats.user.application.service;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.in.PermissionUseCase;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService implements PermissionUseCase {

    private final PermissionRepositoryPort permissionRepository;

    public PermissionService(PermissionRepositoryPort permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> list(Long moduleId, Boolean active) {
        if (moduleId == null && active == null) {
            return permissionRepository.findAll();
        }
        return permissionRepository.findAll(moduleId, active);
    }
}

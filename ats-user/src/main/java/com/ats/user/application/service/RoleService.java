package com.ats.user.application.service;

import com.ats.user.domain.exception.PermissionNotFoundException;
import com.ats.user.domain.exception.RoleAlreadyExistsException;
import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService implements RoleUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;

    public RoleService(RoleRepositoryPort roleRepository,
                       PermissionRepositoryPort permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Role create(Role role) {
        String normalizedName = normalizeName(role.getName());
        if (roleRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new RoleAlreadyExistsException("Role already exists: " + normalizedName);
        }

        role.setName(normalizedName);
        role.setActive(role.isActive());
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    @Override
    public List<Role> list() {
        return roleRepository.findAll();
    }

    @Override
    public Role getById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + id));
    }

    @Override
    public Role update(Long id, Role role) {
        Role current = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + id));

        String normalizedName = normalizeName(role.getName());
        roleRepository.findByNameIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new RoleAlreadyExistsException("Role already exists: " + normalizedName);
                });

        current.setName(normalizedName);
        current.setDescription(role.getDescription());
        current.setActive(role.isActive());
        current.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(current);
    }

    @Override
    public void delete(Long id) {
        Role current = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + id));
        current.setActive(false);
        current.setUpdatedAt(LocalDateTime.now());
        roleRepository.save(current);
    }

    @Override
    public Role assignPermissions(Long roleId, List<Long> permissionIds) {
        if (permissionIds == null) {
            throw new IllegalArgumentException("Permission ids are required");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(permissionIds);
        if (uniqueIds.isEmpty()) {
            throw new IllegalArgumentException("At least one permission id is required");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleId));

        List<Long> normalizedIds = uniqueIds.stream().toList();
        var permissions = permissionRepository.findAllByIds(normalizedIds);
        if (permissions.size() != normalizedIds.size()) {
            throw new PermissionNotFoundException("One or more permissions were not found");
        }

        role.getPermissions().clear();
        permissions.forEach(role::addPermission);
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);
    }

    @Override
    public Role updateStatus(Long id, boolean active) {
        Role current = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + id));
        current.setActive(active);
        current.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(current);
    }

    @Override
    public Role deletePermissions(Long roleId, List<Long> permissionIds) {
        Role role= roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleId));
        Set<Long> removeRole= new HashSet<>(permissionIds);
        boolean changed= role.getPermissions().removeIf(p->removeRole.contains(p.getId()));
        if(!changed) {
            throw new PermissionNotFoundException(
            "Permission " + permissionIds + " is not assigned to role " + roleId
            );
        }
        role.setUpdatedAt(LocalDateTime.now());
        return roleRepository.save(role);


    }

    private String normalizeName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }
        return roleName.trim().toUpperCase();
    }
}

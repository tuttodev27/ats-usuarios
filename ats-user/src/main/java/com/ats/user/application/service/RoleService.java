package com.ats.user.application.service;

import com.ats.user.domain.exception.RoleAlreadyExistsException;
import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.in.RoleUseCase;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RoleService implements RoleUseCase {

    private final RoleRepositoryPort roleRepository;

    public RoleService(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
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

    private String normalizeName(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name is required");
        }
        return roleName.trim().toUpperCase();
    }
}

package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    Role save(Role role);
    Optional<Role> findById(Long id);
    List<Role> findAll();
    boolean existsByNameIgnoreCase(String roleName);
    Optional<Role> findByNameIgnoreCase(String roleName);
    Optional<Role> findActiveByName(String roleName);
    List<String> listActiveRoleNames();
}

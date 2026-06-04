package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepositoryPort {
    List<Permission> findAllByIds(List<Long> ids);
    List<Permission> findAll();
    Optional<Permission> findById(Long id);
}

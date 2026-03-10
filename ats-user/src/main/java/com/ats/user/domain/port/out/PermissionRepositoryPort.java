package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Permission;

import java.util.List;

public interface PermissionRepositoryPort {
    List<Permission> findAllByIds(List<Long> ids);
}

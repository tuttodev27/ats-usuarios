package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Permission;

import java.util.List;

public interface PermissionUseCase {
    List<Permission> list();
}

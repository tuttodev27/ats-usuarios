package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Role;

import java.util.List;

public interface RoleUseCase {
    Role create(Role role);
    List<Role> list();
    Role getById(Long id);
    Role update(Long id, Role role);
    void delete(Long id);
}

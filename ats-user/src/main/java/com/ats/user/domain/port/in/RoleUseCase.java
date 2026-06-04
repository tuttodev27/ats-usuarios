package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.Role;

import java.util.List;

public interface RoleUseCase {
    Role create(Role role);
    List<Role> list();
    Page<Role> listRoles(String search, Boolean active, PageQuery pageQuery);
    Role getById(Long id);
    Role update(Long id, Role role);
    void delete(Long id);
    Role assignPermissions(Long roleId, List<Long> permissionIds);
    Role updateStatus(Long id, boolean active);
    Role deletePermissions(Long roleId, List<Long> permissionIds);
    void removePermission(Long roleId, Long permissionId);
}

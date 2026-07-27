package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;

import java.util.List;

public interface UserUseCase {
    User create(User user, Long roleId, String rawPassword);
    User getById(Long id);
    Page<User> listUsers(String search, Boolean active, PageQuery pageQuery);
    List<Role> listAvailableRoles();
    User update(Long id, User user, Long roleId);
    User changeUserRole(Long userId, Long roleId);
    User delete(Long id);
    User updateStatus(Long id, boolean active);
}

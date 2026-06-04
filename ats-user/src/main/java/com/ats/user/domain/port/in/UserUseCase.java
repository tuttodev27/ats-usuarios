package com.ats.user.domain.port.in;

import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;

import java.util.List;

public interface UserUseCase {
    User create(User user, String roleName, String rawPassword);
    User getById(Long id);
    List<User> listUsers(Boolean active);
    List<Role> listAvailableRoles();
    User update(Long id, User user);
    void delete(Long id);
}

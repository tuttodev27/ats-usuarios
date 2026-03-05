package com.ats.user.domain.port.in;

import com.ats.user.domain.model.User;

import java.util.List;

public interface UserUseCase {
    User create(User user);
    User getById(Long id);
    List<User> listActive();
    User update(Long id, User user);
    void delete(Long id);
}

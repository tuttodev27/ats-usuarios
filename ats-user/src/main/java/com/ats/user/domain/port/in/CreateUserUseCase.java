package com.ats.user.domain.port.in;

import com.ats.user.domain.model.User;

public interface CreateUserUseCase {
    User create(User user);
}

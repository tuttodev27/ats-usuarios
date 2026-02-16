package com.ats.user.domain.port.in;

import com.ats.user.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByLastName(String lastName);
}

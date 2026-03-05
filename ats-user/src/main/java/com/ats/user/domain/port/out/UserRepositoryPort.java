package com.ats.user.domain.port.out;

import com.ats.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAllActive();
    boolean existsById(Long id);
    void delete(Long id);
}

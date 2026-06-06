package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();
    List<User> findAllByActive(boolean active);
    Page<User> searchUsers(String search, Boolean active, PageQuery pageQuery);
    boolean existsById(Long id);
    User delete(Long id);
}

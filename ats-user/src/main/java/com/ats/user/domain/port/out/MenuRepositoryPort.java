package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepositoryPort {
    Menu save(Menu menu);
    Optional<Menu> findById(Long id);
    List<Menu> findAll();
    boolean existsByPathIgnoreCase(String path);
    Optional<Menu> findByPathIgnoreCase(String path);
}

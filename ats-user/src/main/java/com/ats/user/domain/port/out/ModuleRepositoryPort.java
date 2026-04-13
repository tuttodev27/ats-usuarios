package com.ats.user.domain.port.out;

import com.ats.user.domain.model.Module;

import java.util.List;
import java.util.Optional;

public interface ModuleRepositoryPort {
    Module save(Module module);
    Optional<Module> findById(Long id);
    List<Module> findAll();
    boolean existsByCodeIgnoreCase(String code);
    Optional<Module> findByCodeIgnoreCase(String code);
}

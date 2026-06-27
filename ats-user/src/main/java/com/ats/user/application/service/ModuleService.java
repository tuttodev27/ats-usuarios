package com.ats.user.application.service;

import com.ats.user.domain.exception.ModuleAlreadyExistsException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.in.ModuleUseCase;
import com.ats.user.domain.port.out.ModuleRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ModuleService implements ModuleUseCase {
    private final ModuleRepositoryPort moduleRepository;

    public ModuleService(ModuleRepositoryPort moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Override
    @Transactional
    public Module createModule(Module module) {
        String normalizedCode = normalizeCode(module.getCode());
        if (moduleRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new ModuleAlreadyExistsException("Module already exists: " + normalizedCode);
        }

        module.setCode(normalizedCode);
        module.setName(normalizeName(module.getName()));
        module.setDescription(normalizeDescription(module.getDescription()));
        module.setActive(module.isActive());
        module.setCreatedAt(LocalDateTime.now());
        module.setUpdatedAt(LocalDateTime.now());
        return moduleRepository.save(module);
    }

    @Override
    public List<Module> getModules() {
        return moduleRepository.findAll();
    }

    @Override
    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found: " + id));
    }

    @Override
    @Transactional
    public Module updateModule(Long id, Module module) {
        Module current = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found: " + id));

        String normalizedCode = normalizeCode(module.getCode());
        moduleRepository.findByCodeIgnoreCase(normalizedCode)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ModuleAlreadyExistsException("Module already exists: " + normalizedCode);
                });

        current.setCode(normalizedCode);
        current.setName(normalizeName(module.getName()));
        current.setDescription(normalizeDescription(module.getDescription()));
        current.setUpdatedAt(LocalDateTime.now());
        return moduleRepository.save(current);
    }

    @Override
    @Transactional
    public Module updateModuleStatus(Long id, boolean active) {
        Module current = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found: " + id));
        current.setActive(active);
        current.setUpdatedAt(LocalDateTime.now());
        return moduleRepository.save(current);
    }

    @Override
    @Transactional
    public void deleteModuleById(Long id) {
        Module current = moduleRepository.findById(id)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found: " + id));
        current.setActive(false);
        current.setUpdatedAt(LocalDateTime.now());
        moduleRepository.save(current);
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Module code is required");
        }
        return code.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Module name is required");
        }
        return name.trim();
    }

    private String normalizeDescription(String description) {
        return description == null ? null : description.trim();
    }
}

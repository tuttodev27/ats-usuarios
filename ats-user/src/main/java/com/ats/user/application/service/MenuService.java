package com.ats.user.application.service;

import com.ats.user.domain.exception.MenuAlreadyExistsException;
import com.ats.user.domain.exception.MenuNotFoundException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Menu;
import com.ats.user.domain.port.in.MenuUseCase;
import com.ats.user.domain.port.out.MenuRepositoryPort;
import com.ats.user.domain.port.out.ModuleRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MenuService implements MenuUseCase {

    private final MenuRepositoryPort menuRepository;
    private final ModuleRepositoryPort moduleRepository;

    public MenuService(MenuRepositoryPort menuRepository, ModuleRepositoryPort moduleRepository) {
        this.menuRepository = menuRepository;
        this.moduleRepository = moduleRepository;
    }

    @Override
    public Menu create(Menu menu) {
        String normalizedPath = normalizePath(menu.getPath());
        if (menuRepository.existsByPathIgnoreCase(normalizedPath)) {
            throw new MenuAlreadyExistsException("Menu already exists for path: " + normalizedPath);
        }

        Long moduleId = validateModuleId(menu.getModuleId());
        menu.setTitle(normalizeTitle(menu.getTitle()));
        menu.setPath(normalizedPath);
        menu.setIcon(menu.getIcon());
        menu.setRequiredPermissionCode(normalizePermissionCode(menu.getRequiredPermissionCode()));
        menu.setModuleId(moduleId);
        menu.setCreatedAt(LocalDateTime.now());
        menu.setUpdatedAt(LocalDateTime.now());
        return menuRepository.save(menu);
    }

    @Override
    public List<Menu> list() {
        return menuRepository.findAll();
    }

    @Override
    public Menu getById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + id));
    }

    @Override
    public Menu update(Long id, Menu menu) {
        Menu current = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + id));

        String normalizedPath = normalizePath(menu.getPath());
        menuRepository.findByPathIgnoreCase(normalizedPath)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new MenuAlreadyExistsException("Menu already exists for path: " + normalizedPath);
                });

        Long moduleId = validateModuleId(menu.getModuleId());
        current.setTitle(normalizeTitle(menu.getTitle()));
        current.setPath(normalizedPath);
        current.setIcon(menu.getIcon());
        current.setOrderIndex(menu.getOrderIndex());
        current.setRequiredPermissionCode(normalizePermissionCode(menu.getRequiredPermissionCode()));
        current.setModuleId(moduleId);
        current.setUpdatedAt(LocalDateTime.now());
        return menuRepository.save(current);
    }

    @Override
    public Menu updateStatus(Long id, boolean active) {
        Menu current = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + id));
        current.setActive(active);
        current.setUpdatedAt(LocalDateTime.now());
        return menuRepository.save(current);
    }

    @Override
    public void delete(Long id) {
        Menu current = menuRepository.findById(id)
                .orElseThrow(() -> new MenuNotFoundException("Menu not found: " + id));
        current.setActive(false);
        current.setUpdatedAt(LocalDateTime.now());
        menuRepository.save(current);
    }

    private Long validateModuleId(Long moduleId) {
        if (moduleId == null) {
            throw new IllegalArgumentException("Module id is required");
        }
        moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ModuleNotFoundException("Module not found: " + moduleId));
        return moduleId;
    }

    private String normalizeTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Menu title is required");
        }
        return title.trim();
    }

    private String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Menu path is required");
        }
        return path.trim();
    }

    private String normalizePermissionCode(String permissionCode) {
        if (permissionCode == null || permissionCode.isBlank()) {
            return null;
        }
        return permissionCode.trim().toUpperCase();
    }
}

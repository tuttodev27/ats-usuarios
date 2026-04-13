package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Menu;
import com.ats.user.domain.port.out.MenuRepositoryPort;
import com.ats.user.infrastructure.out.entity.MenuEntity;
import com.ats.user.infrastructure.out.repository.MenuJpaRepository;
import com.ats.user.infrastructure.out.repository.ModuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MenuRepositoryAdapter implements MenuRepositoryPort {

    private final MenuJpaRepository menuJpaRepository;
    private final ModuleJpaRepository moduleJpaRepository;

    @Override
    public Menu save(Menu menu) {
        MenuEntity entity = menu.getId() != null
                ? menuJpaRepository.findById(menu.getId()).orElse(new MenuEntity())
                : new MenuEntity();

        entity.setTitle(menu.getTitle());
        entity.setPath(menu.getPath());
        entity.setOrderIndex(menu.getOrderIndex());
        entity.setRequiredPermissionCode(menu.getRequiredPermissionCode());
        entity.setActive(menu.isActive());
        entity.setCreatedAt(menu.getCreatedAt());
        entity.setUpdatedAt(menu.getUpdatedAt());
        entity.setModule(moduleJpaRepository.findById(menu.getModuleId()).orElseThrow());

        return toDomain(menuJpaRepository.save(entity));
    }

    @Override
    public Optional<Menu> findById(Long id) {
        return menuJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Menu> findAll() {
        return menuJpaRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByPathIgnoreCase(String path) {
        return menuJpaRepository.existsByPathIgnoreCase(path);
    }

    @Override
    public Optional<Menu> findByPathIgnoreCase(String path) {
        return menuJpaRepository.findByPathIgnoreCase(path).map(this::toDomain);
    }

    private Menu toDomain(MenuEntity entity) {
        return Menu.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .path(entity.getPath())
                .moduleId(entity.getModule() != null ? entity.getModule().getId() : null)
                .orderIndex(entity.getOrderIndex())
                .requiredPermissionCode(entity.getRequiredPermissionCode())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

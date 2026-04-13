package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.out.ModuleRepositoryPort;
import com.ats.user.infrastructure.out.entity.ModuleEntity;
import com.ats.user.infrastructure.out.repository.ModuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository moduleJpaRepository;

    @Override
    public Module save(Module module) {
        ModuleEntity entity = module.getId() != null
                ? moduleJpaRepository.findById(module.getId()).orElse(new ModuleEntity())
                : new ModuleEntity();

        entity.setCode(module.getCode());
        entity.setName(module.getName());
        entity.setDescription(module.getDescription());
        entity.setActive(module.isActive());
        entity.setCreatedAt(module.getCreatedAt());
        entity.setUpdatedAt(module.getUpdatedAt());
        entity.setCreatedBy(module.getCreatedBy());
        entity.setUpdatedBy(module.getUpdatedBy());

        return toDomain(moduleJpaRepository.save(entity));
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Module> findAll() {
        return moduleJpaRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCodeIgnoreCase(String code) {
        return moduleJpaRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public Optional<Module> findByCodeIgnoreCase(String code) {
        return moduleJpaRepository.findByCodeIgnoreCase(code).map(this::toDomain);
    }

    private Module toDomain(ModuleEntity entity) {
        return Module.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .active(Boolean.TRUE.equals(entity.getActive()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}

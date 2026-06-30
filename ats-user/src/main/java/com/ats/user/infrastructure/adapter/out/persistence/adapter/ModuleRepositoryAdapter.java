package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.out.ModuleRepositoryPort;
import com.ats.user.infrastructure.adapter.out.persistence.entity.ModuleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.ModuleMapper;
import com.ats.user.infrastructure.adapter.out.persistence.repository.ModuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ModuleRepositoryAdapter implements ModuleRepositoryPort {

    private final ModuleJpaRepository moduleJpaRepository;
    private final ModuleMapper moduleMapper;

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

        return moduleMapper.toDomain(moduleJpaRepository.save(entity));
    }

    @Override
    public Optional<Module> findById(Long id) {
        return moduleJpaRepository.findById(id).map(moduleMapper::toDomain);
    }

    @Override
    public List<Module> findAll() {
        return moduleJpaRepository.findAllByOrderByIdAsc().stream()
                .map(moduleMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCodeIgnoreCase(String code) {
        return moduleJpaRepository.existsByCodeIgnoreCase(code);
    }

    @Override
    public Optional<Module> findByCodeIgnoreCase(String code) {
        return moduleJpaRepository.findByCodeIgnoreCase(code).map(moduleMapper::toDomain);
    }
}

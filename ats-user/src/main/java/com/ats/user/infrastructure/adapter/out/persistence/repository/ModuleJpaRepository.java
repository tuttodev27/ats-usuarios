package com.ats.user.infrastructure.adapter.out.persistence.repository;

import com.ats.user.infrastructure.adapter.out.persistence.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModuleJpaRepository extends JpaRepository<ModuleEntity, Long> {
    List<ModuleEntity> findAllByOrderByIdAsc();
    boolean existsByCodeIgnoreCase(String code);
    Optional<ModuleEntity> findByCodeIgnoreCase(String code);
}

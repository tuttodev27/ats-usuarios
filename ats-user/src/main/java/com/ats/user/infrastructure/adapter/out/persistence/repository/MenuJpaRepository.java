package com.ats.user.infrastructure.adapter.out.persistence.repository;

import com.ats.user.infrastructure.adapter.out.persistence.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuJpaRepository extends JpaRepository<MenuEntity, Long> {
    List<MenuEntity> findAllByOrderByIdAsc();
    boolean existsByPathIgnoreCase(String path);
    Optional<MenuEntity> findByPathIgnoreCase(String path);
}

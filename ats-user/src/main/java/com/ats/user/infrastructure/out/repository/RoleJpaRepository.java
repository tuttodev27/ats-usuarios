package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByNameIgnoreCaseAndActiveTrue(String name);
    List<RoleEntity> findAllByActiveTrueOrderByNameAsc();
}

package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.RoleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    @EntityGraph(attributePaths = "permissions")
    Optional<RoleEntity> findWithPermissionsById(Long id);

    @EntityGraph(attributePaths = "permissions")
    List<RoleEntity> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCase(String name);
    Optional<RoleEntity> findByNameIgnoreCase(String name);
    Optional<RoleEntity> findByNameIgnoreCaseAndActiveTrue(String name);
    List<RoleEntity> findAllByActiveTrueOrderByNameAsc();
}

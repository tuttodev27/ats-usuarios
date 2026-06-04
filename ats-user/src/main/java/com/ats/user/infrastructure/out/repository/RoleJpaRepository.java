package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.RoleEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    @EntityGraph(attributePaths = {"rolePermissions", "rolePermissions.permission", "rolePermissions.permission.module"})
    Optional<RoleEntity> findWithPermissionsById(Long id);

    @EntityGraph(attributePaths = {"rolePermissions", "rolePermissions.permission", "rolePermissions.permission.module"})
    List<RoleEntity> findAllByOrderByIdAsc();

    boolean existsByNameIgnoreCase(String name);
    Optional<RoleEntity> findByNameIgnoreCase(String name);
    Optional<RoleEntity> findByNameIgnoreCaseAndActiveTrue(String name);
    @EntityGraph(attributePaths = {"rolePermissions", "rolePermissions.permission"})
    List<RoleEntity> findAllByActiveTrueOrderByNameAsc();
}

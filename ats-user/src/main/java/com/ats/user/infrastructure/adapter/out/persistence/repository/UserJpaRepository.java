package com.ats.user.infrastructure.adapter.out.persistence.repository;

import com.ats.user.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    @EntityGraph(attributePaths = {
            "roles",
            "roles.rolePermissions",
            "roles.rolePermissions.permission"
    })
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findAllByActiveTrue();
    List<UserEntity> findAllByActive(boolean active);
    Optional<UserEntity> findByIdAndActiveTrue(Long id);
    boolean existsById(Long id);
}

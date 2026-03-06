package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<UserEntity> findAllByActiveTrue();
    Optional<UserEntity> findByIdAndActiveTrue(Long id);
    boolean existsById(Long id);
}

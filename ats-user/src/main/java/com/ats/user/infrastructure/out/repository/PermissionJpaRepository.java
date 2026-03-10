package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
}

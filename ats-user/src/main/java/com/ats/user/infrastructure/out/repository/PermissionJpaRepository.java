package com.ats.user.infrastructure.out.repository;

import com.ats.user.infrastructure.out.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    @Query("""
            select p from PermissionEntity p
            join fetch p.module m
            order by m.code asc, p.code asc
            """)
    List<PermissionEntity> findAllWithModuleOrderByModuleCodeAscCodeAsc();
}

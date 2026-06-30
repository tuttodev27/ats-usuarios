package com.ats.user.infrastructure.adapter.out.persistence.repository;

import com.ats.user.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {
    @Query("""
            select p from PermissionEntity p
            join fetch p.module m
            where (:moduleId is null or m.id = :moduleId)
            and (:active is null or p.active = :active)
            order by m.code asc, p.code asc
            """)
    List<PermissionEntity> findAllFiltered(Long moduleId, Boolean active);

    @Query("""
            select p from PermissionEntity p
            join fetch p.module m
            order by m.code asc, p.code asc
            """)
    List<PermissionEntity> findAllWithModuleOrderByModuleCodeAscCodeAsc();
}

package com.ats.user.infrastructure.adapter.out.persistence.mapper;

import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RoleEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class RoleMapperTest {

    @Autowired
    private RoleMapper roleMapper;

    @Test
    void toDomain_shouldMapCreatedByAndUpdatedBy() {
        RoleEntity entity = new RoleEntity();
        entity.setId(1L);
        entity.setName("ADMIN");
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setCreatedBy(10L);
        entity.setUpdatedBy(20L);

        Role result = roleMapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(10L, result.getCreatedBy());
        assertEquals(20L, result.getUpdatedBy());
    }

    @Test
    void toDomain_shouldHandleNullCreatedByAndUpdatedBy() {
        RoleEntity entity = new RoleEntity();
        entity.setId(2L);
        entity.setName("RECRUITER");
        entity.setActive(true);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        Role result = roleMapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(null, result.getCreatedBy());
        assertEquals(null, result.getUpdatedBy());
    }

    @Test
    void toDomain_shouldMapAllBasicFields() {
        LocalDateTime now = LocalDateTime.now();
        RoleEntity entity = new RoleEntity();
        entity.setId(3L);
        entity.setName("MANAGER");
        entity.setDescription("Manager role");
        entity.setActive(true);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(5L);
        entity.setUpdatedBy(6L);

        Role result = roleMapper.toDomain(entity);

        assertEquals(3L, result.getId());
        assertEquals("MANAGER", result.getName());
        assertEquals("Manager role", result.getDescription());
        assertEquals(true, result.isActive());
        assertEquals(now, result.getCreatedAt());
        assertEquals(now, result.getUpdatedAt());
        assertEquals(5L, result.getCreatedBy());
        assertEquals(6L, result.getUpdatedBy());
    }
}

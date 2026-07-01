package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.domain.model.Permission;
import com.ats.user.infrastructure.adapter.out.persistence.entity.ModuleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.PermissionMapper;
import com.ats.user.infrastructure.adapter.out.persistence.repository.PermissionJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionRepositoryAdapterTest {

    @Mock
    private PermissionJpaRepository permissionJpaRepository;

    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionRepositoryAdapter permissionRepositoryAdapter;

    @Test
    void findAllShouldReturnAllPermissionsMapped() {
        var moduleEntity = moduleEntity(10L, "USERS");
        var entities = List.of(
                permissionEntity(1L, "users:read", moduleEntity),
                permissionEntity(2L, "users:write", moduleEntity)
        );
        var domains = List.of(
                domainPermission(1L, "users:read", 10L),
                domainPermission(2L, "users:write", 10L)
        );

        when(permissionJpaRepository.findAllWithModuleOrderByModuleCodeAscCodeAsc()).thenReturn(entities);
        when(permissionMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(permissionMapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        var result = permissionRepositoryAdapter.findAll();

        assertEquals(2, result.size());
        verify(permissionJpaRepository).findAllWithModuleOrderByModuleCodeAscCodeAsc();
    }

    @Test
    void findAllWithModuleIdAndActiveShouldDelegateToFindAllFiltered() {
        var moduleEntity = moduleEntity(10L, "USERS");
        var entities = List.of(permissionEntity(1L, "users:read", moduleEntity));
        var domains = List.of(domainPermission(1L, "users:read", 10L));

        when(permissionJpaRepository.findAllFiltered(10L, true)).thenReturn(entities);
        when(permissionMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));

        var result = permissionRepositoryAdapter.findAll(10L, true);

        assertEquals(1, result.size());
        verify(permissionJpaRepository).findAllFiltered(10L, true);
    }

    @Test
    void findAllByIdsShouldReturnMappedPermissions() {
        var moduleEntity = moduleEntity(10L, "USERS");
        var entities = List.of(permissionEntity(1L, "users:read", moduleEntity));
        var domains = List.of(domainPermission(1L, "users:read", 10L));

        when(permissionJpaRepository.findAllById(List.of(1L))).thenReturn(entities);
        when(permissionMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));

        var result = permissionRepositoryAdapter.findAllByIds(List.of(1L));

        assertEquals(1, result.size());
    }

    @Test
    void findByIdShouldReturnPermissionWhenFound() {
        var moduleEntity = moduleEntity(10L, "USERS");
        var entity = permissionEntity(1L, "users:read", moduleEntity);
        var domain = domainPermission(1L, "users:read", 10L);

        when(permissionJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(permissionMapper.toDomain(entity)).thenReturn(domain);

        var result = permissionRepositoryAdapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("users:read", result.get().getCode());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(permissionJpaRepository.findById(99L)).thenReturn(Optional.empty());

        var result = permissionRepositoryAdapter.findById(99L);

        assertTrue(result.isEmpty());
    }

    private ModuleEntity moduleEntity(Long id, String code) {
        return ModuleEntity.builder()
                .id(id)
                .code(code)
                .name(code)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PermissionEntity permissionEntity(Long id, String code, ModuleEntity module) {
        return PermissionEntity.builder()
                .id(id)
                .code(code)
                .name(code)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .module(module)
                .build();
    }

    private Permission domainPermission(Long id, String code, Long moduleId) {
        return Permission.builder()
                .id(id)
                .code(code)
                .name(code)
                .moduleId(moduleId)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

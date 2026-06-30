package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.RoleMapper;
import com.ats.user.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryAdapterTest {

    @Mock
    private RoleJpaRepository roleJpaRepository;

    @Mock
    private RolePermissionSyncSupport rolePermissionSyncSupport;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleRepositoryAdapter roleRepositoryAdapter;

    @Test
    void listActiveRoleNamesShouldReturnOnlyActiveRoleNamesOrderedByRepository() {
        when(roleJpaRepository.findAllByActiveTrueOrderByNameAsc()).thenReturn(List.of(
                roleEntity(1L, "ADMIN", true),
                roleEntity(2L, "RECRUITER", true)
        ));

        List<String> roleNames = roleRepositoryAdapter.listActiveRoleNames();

        assertEquals(List.of("ADMIN", "RECRUITER"), roleNames);
        verify(roleJpaRepository).findAllByActiveTrueOrderByNameAsc();
    }

    private RoleEntity roleEntity(Long id, String name, boolean active) {
        return RoleEntity.builder()
                .id(id)
                .name(name)
                .description("desc")
                .active(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

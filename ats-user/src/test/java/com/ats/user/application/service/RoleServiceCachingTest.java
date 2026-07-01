package com.ats.user.application.service;

import com.ats.user.domain.model.Role;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.repository.RoleJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RoleServiceCachingTest {

    @Autowired
    private RoleService roleService;

    @MockitoBean
    private RoleJpaRepository roleJpaRepository;

    @Test
    void getById_shouldReturnCachedResultOnSecondCall() {
        RoleEntity entity = new RoleEntity();
        entity.setId(1L);
        entity.setName("ADMIN");
        entity.setActive(true);
        when(roleJpaRepository.findWithPermissionsById(1L)).thenReturn(Optional.of(entity));

        Role first = roleService.getById(1L);
        Role second = roleService.getById(1L);

        assertEquals("ADMIN", first.getName());
        assertEquals("ADMIN", second.getName());
        verify(roleJpaRepository, times(1)).findWithPermissionsById(1L);
    }

    @Test
    void list_shouldReturnCachedResultOnSecondCall() {
        when(roleJpaRepository.findAllByOrderByIdAsc()).thenReturn(java.util.List.of());

        roleService.list();
        roleService.list();

        verify(roleJpaRepository, times(1)).findAllByOrderByIdAsc();
    }
}

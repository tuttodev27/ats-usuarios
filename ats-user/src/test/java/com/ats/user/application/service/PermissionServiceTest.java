package com.ats.user.application.service;

import com.ats.user.domain.model.Permission;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepositoryPort permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void listShouldReturnPermissionCatalog() {
        when(permissionRepository.findAll()).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build(),
                Permission.builder().id(2L).code("ROLE_READ").name("Ver Roles").active(true).build()
        ));

        List<Permission> permissions = permissionService.list(null, null);

        assertEquals(2, permissions.size());
        assertEquals("USER_READ", permissions.getFirst().getCode());
    }

    @Test
    void listShouldFilterByModuleId() {
        when(permissionRepository.findAll(1L, null)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build()
        ));

        List<Permission> permissions = permissionService.list(1L, null);

        assertEquals(1, permissions.size());
        assertEquals("USER_READ", permissions.getFirst().getCode());
    }

    @Test
    void listShouldFilterByActive() {
        when(permissionRepository.findAll(null, true)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build()
        ));

        List<Permission> permissions = permissionService.list(null, true);

        assertEquals(1, permissions.size());
        assertTrue(permissions.getFirst().isActive());
    }

    @Test
    void listShouldFilterByModuleIdAndActive() {
        when(permissionRepository.findAll(1L, true)).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").name("Ver Usuarios").active(true).build()
        ));

        List<Permission> permissions = permissionService.list(1L, true);

        assertEquals(1, permissions.size());
    }

    @Test
    void listWithoutFiltersShouldFallbackToFindAll() {
        when(permissionRepository.findAll()).thenReturn(List.of(
                Permission.builder().id(1L).code("USER_READ").active(true).build(),
                Permission.builder().id(2L).code("ROLE_READ").active(true).build()
        ));

        List<Permission> permissions = permissionService.list(null, null);

        assertEquals(2, permissions.size());
    }
}

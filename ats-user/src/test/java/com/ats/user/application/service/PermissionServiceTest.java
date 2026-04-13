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
                Permission.builder().id(1L).code("USER_READ").active(true).build(),
                Permission.builder().id(2L).code("ROLE_READ").active(true).build()
        ));

        List<Permission> permissions = permissionService.list();

        assertEquals(2, permissions.size());
        assertEquals("USER_READ", permissions.getFirst().getCode());
    }
}

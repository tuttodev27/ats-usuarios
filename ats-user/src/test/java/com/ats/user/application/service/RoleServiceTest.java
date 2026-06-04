package com.ats.user.application.service;

import com.ats.user.domain.exception.PermissionNotFoundException;
import com.ats.user.domain.exception.RoleAlreadyExistsException;
import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.model.Permission;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.port.out.PermissionRepositoryPort;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private PermissionRepositoryPort permissionRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void createShouldSaveRoleWhenNameIsAvailable() {
        Role input = roleInput(" recruiter ");
        when(roleRepository.existsByNameIgnoreCase("RECRUITER")).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role created = roleService.create(input);

        assertEquals("RECRUITER", created.getName());
        assertFalse(created.isActive());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(roleRepository).save(input);
    }

    @Test
    void createShouldFailWhenRoleNameAlreadyExists() {
        Role input = roleInput("ADMIN");
        when(roleRepository.existsByNameIgnoreCase("ADMIN")).thenReturn(true);

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.create(input));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> roleService.getById(99L));
    }

    @Test
    void updateShouldModifyRoleFields() {
        Role current = roleExisting(1L, "ADMIN", true);
        Role updateInput = Role.builder()
                .name("recruiter")
                .description("nuevo")
                .build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(roleRepository.findByNameIgnoreCase("RECRUITER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role updated = roleService.update(1L, updateInput);

        assertEquals("RECRUITER", updated.getName());
        assertEquals("nuevo", updated.getDescription());
        assertTrue(updated.isActive());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateShouldFailWhenNameBelongsToAnotherRole() {
        Role current = roleExisting(1L, "ADMIN", true);
        Role another = roleExisting(2L, "RECRUITER", true);
        Role updateInput = Role.builder().name("recruiter").build();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(roleRepository.findByNameIgnoreCase("RECRUITER")).thenReturn(Optional.of(another));

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.update(1L, updateInput));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deleteShouldSetRoleInactive() {
        Role current = roleExisting(1L, "ADMIN", true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        roleService.delete(1L);

        assertFalse(current.isActive());
        assertNotNull(current.getUpdatedAt());
        verify(roleRepository).save(current);
    }

    @Test
    void assignPermissionsShouldReplacePermissionsAndSaveRole() {
        Role role = roleExisting(1L, "ADMIN", true);
        role.setPermissions(new HashSet<>(Set.of(permission(9L, "OLD_PERMISSION"))));
        List<Long> ids = List.of(1L, 2L, 2L);
        List<Permission> resolved = List.of(
                permission(1L, "USER_READ"),
                permission(2L, "USER_CREATE")
        );

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllByIds(List.of(1L, 2L))).thenReturn(resolved);
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role updated = roleService.assignPermissions(1L, ids);

        assertEquals(2, updated.getPermissions().size());
        assertEquals(Set.of(1L, 2L), updated.getPermissions().stream().map(Permission::getId).collect(java.util.stream.Collectors.toSet()));
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void assignPermissionsShouldFailWhenAnyPermissionDoesNotExist() {
        Role role = roleExisting(1L, "ADMIN", true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllByIds(List.of(1L, 2L))).thenReturn(List.of(permission(1L, "USER_READ")));

        assertThrows(PermissionNotFoundException.class, () -> roleService.assignPermissions(1L, List.of(1L, 2L)));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void updateStatusShouldChangeActiveFlag() {
        Role role = roleExisting(1L, "ADMIN", true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role updated = roleService.updateStatus(1L, false);

        assertFalse(updated.isActive());
        assertNotNull(updated.getUpdatedAt());
        verify(roleRepository).save(role);
    }

    @Test
    void updateStatusShouldThrowWhenRoleDoesNotExist() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.updateStatus(99L, false));
        verify(roleRepository, never()).save(any());
    }

    @Test
    void deletePermissionsShouldRemovePermissionsFromVisibleRoleAndSave() {
        Role role = roleExisting(1L, "ADMIN", true);
        role.setPermissions(new HashSet<>(Set.of(
                permission(1L, "USER_READ"),
                permission(2L, "USER_UPDATE")
        )));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Role updated = roleService.deletePermissions(1L, List.of(1L));

        assertEquals(1, updated.getPermissions().size());
        assertTrue(updated.getPermissions().stream().noneMatch(permission -> permission.getId().equals(1L)));
        assertNotNull(updated.getUpdatedAt());
        verify(roleRepository).save(role);
    }

    private Role roleInput(String name) {
        return Role.builder()
                .name(name)
                .description("desc")
                .active(false)
                .build();
    }

    private Role roleExisting(Long id, String name, boolean active) {
        return Role.builder()
                .id(id)
                .name(name)
                .description("desc")
                .active(active)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .permissions(new HashSet<>())
                .build();
    }

    private Permission permission(Long id, String code) {
        return Permission.builder()
                .id(id)
                .code(code)
                .active(true)
                .build();
    }
}

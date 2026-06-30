package com.ats.user.infrastructure.adapter.in.web.security;

import com.ats.user.infrastructure.adapter.out.persistence.entity.PermissionEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RolePermissionEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.RolePermissionId;
import com.ats.user.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.ats.user.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.ats.user.support.UserDumpData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {

    @Mock
    private UserJpaRepository userRepository;

    @InjectMocks
    private AuthUserDetailsService authUserDetailsService;

    @Test
    void loadUserByUsernameShouldMapActiveDumpRoles() {
        RoleEntity admin = UserDumpData.entityRole("ADMIN", true);
        RoleEntity recruiter = UserDumpData.entityRole("ROLE_RECRUITER", true);
        UserEntity userEntity = UserDumpData.entityUserForAuth(true, Set.of(admin, recruiter));

        when(userRepository.findByEmail("admin@ats.local")).thenReturn(java.util.Optional.of(userEntity));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("admin@ats.local");

        assertEquals("admin@ats.local", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RECRUITER")));
        assertFalse(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldIncludeActivePermissionAuthorities() {
        RoleEntity admin = UserDumpData.entityRole("ADMIN", true);
        PermissionEntity createUser = permissionEntity(1L, "USER_CREATE", true);
        PermissionEntity readUser = permissionEntity(2L, "USER_READ", true);

        admin.setRolePermissions(Set.of(
                rolePermission(admin, createUser, true),
                rolePermission(admin, readUser, true)
        ));

        UserEntity userEntity = UserDumpData.entityUserForAuth(true, Set.of(admin));
        when(userRepository.findByEmail("admin@ats.local")).thenReturn(java.util.Optional.of(userEntity));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("admin@ats.local");

        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_CREATE")));
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_READ")));
    }

    @Test
    void loadUserByUsernameShouldIgnoreInactiveOrLogicallyDeletedPermissions() {
        RoleEntity admin = UserDumpData.entityRole("ADMIN", true);
        PermissionEntity activePermission = permissionEntity(1L, "USER_CREATE", true);
        PermissionEntity inactivePermission = permissionEntity(2L, "USER_DELETE", false);
        PermissionEntity logicallyDeletedPermission = permissionEntity(3L, "USER_UPDATE", true);

        admin.setRolePermissions(Set.of(
                rolePermission(admin, activePermission, true),
                rolePermission(admin, inactivePermission, true),
                rolePermission(admin, logicallyDeletedPermission, false)
        ));

        UserEntity userEntity = UserDumpData.entityUserForAuth(true, Set.of(admin));
        when(userRepository.findByEmail("admin@ats.local")).thenReturn(java.util.Optional.of(userEntity));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("admin@ats.local");

        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_CREATE")));
        assertFalse(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_DELETE")));
        assertFalse(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("USER_UPDATE")));
    }

    @Test
    void loadUserByUsernameShouldFallbackToRoleUserWhenNoActiveRoles() {
        RoleEntity inactiveRole = UserDumpData.entityRole("ADMIN", false);
        UserEntity userEntity = UserDumpData.entityUserForAuth(true, Set.of(inactiveRole));

        when(userRepository.findByEmail("admin@ats.local")).thenReturn(java.util.Optional.of(userEntity));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("admin@ats.local");

        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsernameShouldDisableWhenUserIsInactive() {
        UserEntity userEntity = UserDumpData.entityUserForAuth(false, Set.of(UserDumpData.entityRole("ADMIN", true)));

        when(userRepository.findByEmail("admin@ats.local")).thenReturn(java.util.Optional.of(userEntity));

        UserDetails userDetails = authUserDetailsService.loadUserByUsername("admin@ats.local");

        assertFalse(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@ats.local")).thenReturn(java.util.Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> authUserDetailsService.loadUserByUsername("notfound@ats.local"));
    }

    private PermissionEntity permissionEntity(Long id, String code, boolean active) {
        return PermissionEntity.builder()
                .id(id)
                .code(code)
                .active(active)
                .build();
    }

    private RolePermissionEntity rolePermission(RoleEntity role, PermissionEntity permission, boolean active) {
        return RolePermissionEntity.builder()
                .id(new RolePermissionId(role.getId(), permission.getId()))
                .role(role)
                .permission(permission)
                .active(active)
                .build();
    }
}

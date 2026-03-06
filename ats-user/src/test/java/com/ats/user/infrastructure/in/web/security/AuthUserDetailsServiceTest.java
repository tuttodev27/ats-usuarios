package com.ats.user.infrastructure.in.web.security;

import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.entity.UserEntity;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
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
}

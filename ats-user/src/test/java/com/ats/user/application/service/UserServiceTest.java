package com.ats.user.application.service;

import com.ats.user.application.service.password.PasswordPolicy;
import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.exception.RoleNotAvailableException;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.out.PasswordHasherPort;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import com.ats.user.domain.port.out.UserRepositoryPort;
import com.ats.user.support.UserDumpData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private RoleRepositoryPort roleRepository;
    @Mock
    private PasswordHasherPort passwordHasher;
    @Mock
    private PasswordPolicy passwordPolicy;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldSaveUserAndDefaultActiveUsingDumpData() {
        User input = UserDumpData.domainUserCreate();
        Role recruiterRole = UserDumpData.domainRole("RECRUITER");
        recruiterRole.setId(2L);

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(2L)).thenReturn(Optional.of(recruiterRole));
        when(passwordHasher.encode("Clave123")).thenReturn("$2a$10$encodedHash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.create(input, 2L, "Clave123");

        assertNotNull(created);
        assertEquals(true, created.getActive());
        assertEquals("RECRUITER", created.getRoles().iterator().next().getName());
        assertEquals("$2a$10$encodedHash", created.getPasswordHash());
        verify(passwordPolicy).validate("Clave123");
        verify(userRepository).save(input);
    }

    @Test
    void createShouldFailWhenEmailAlreadyExists() {
        User input = UserDumpData.domainUserCreate();
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(UserDumpData.domainUserExisting()));

        assertThrows(EmailAlreadyExistException.class, () -> userService.create(input, 2L, "Clave123"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createShouldFailWhenRoleNotFound() {
        User input = UserDumpData.domainUserCreate();
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoleNotAvailableException.class, () -> userService.create(input, 99L, "Clave123"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createShouldFailWhenRoleIsInactive() {
        User input = UserDumpData.domainUserCreate();
        Role inactiveRole = UserDumpData.domainRole("INACTIVE");
        inactiveRole.setId(3L);
        inactiveRole.setActive(false);
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(3L)).thenReturn(Optional.of(inactiveRole));

        assertThrows(RoleNotAvailableException.class, () -> userService.create(input, 3L, "Clave123"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getByIdShouldReturnUserWhenExists() {
        User existing = UserDumpData.domainUserExisting();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        User result = userService.getById(1L);

        assertEquals("admin@ats.local", result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getByIdShouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    void listUsersShouldReturnAllWhenFilterIsNull() {
        when(userRepository.findAll()).thenReturn(List.of(UserDumpData.domainUserExisting()));
        List<User> users = userService.listUsers(null);

        assertEquals(1, users.size());
        assertEquals("admin@ats.local", users.get(0).getEmail());
    }

    @Test
    void listUsersShouldReturnOnlyActiveWhenFilterIsTrue() {
        when(userRepository.findAllByActive(true)).thenReturn(List.of(UserDumpData.domainUserExisting()));

        List<User> users = userService.listUsers(true);

        assertEquals(1, users.size());
        assertEquals("admin@ats.local", users.get(0).getEmail());
        verify(userRepository).findAllByActive(true);
    }

    @Test
    void listAvailableRolesShouldReturnActiveRoles() {
        var admin = Role.builder().id(1L).name("ADMIN").description("Administrator").active(true).build();
        var recruiter = Role.builder().id(2L).name("RECRUITER").description("Recruiter").active(true).build();
        when(roleRepository.findAllActive()).thenReturn(List.of(admin, recruiter));

        var roles = userService.listAvailableRoles();

        assertEquals(2, roles.size());
        assertEquals("ADMIN", roles.get(0).getName());
        assertEquals("RECRUITER", roles.get(1).getName());
        verify(roleRepository).findAllActive();
    }

    @Test
    void updateShouldModifyEditableFields() {
        User current = UserDumpData.domainUserExisting();
        User update = UserDumpData.domainUpdateInput();

        when(userRepository.findById(1L)).thenReturn(Optional.of(current));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.update(1L, update);

        assertEquals("Pablo Updated", updated.getName());
        assertEquals("Gallegos Updated", updated.getLastName());
        assertEquals("+51", updated.getCountryCode());
        assertEquals("987123123", updated.getPhone());
        assertEquals(false, updated.getActive());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void deleteShouldCallRepositoryDeleteWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).delete(1L);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(userRepository.existsById(10L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.delete(10L));
        verify(userRepository, never()).delete(eq(10L));
    }
}

package com.ats.user.application.service;

import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.User;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createShouldSaveUserAndDefaultActiveUsingDumpData() {
        User input = UserDumpData.domainUserCreate();

        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = userService.create(input);

        assertNotNull(created);
        assertEquals(true, created.getActive());
        assertEquals("RECRUITER", created.getRoles().iterator().next().getName());
        verify(userRepository).save(input);
    }

    @Test
    void createShouldFailWhenEmailAlreadyExists() {
        User input = UserDumpData.domainUserCreate();
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.of(UserDumpData.domainUserExisting()));

        assertThrows(EmailAlreadyExistException.class, () -> userService.create(input));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createShouldFailWhenRoleIsMissing() {
        User input = UserDumpData.domainUserCreate();
        input.setRoles(null);
        when(userRepository.findByEmail(input.getEmail())).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> userService.create(input));
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
    void listActiveShouldReturnDumpUsers() {
        when(userRepository.findAllActive()).thenReturn(List.of(UserDumpData.domainUserExisting()));
        List<User> users = userService.listActive();

        assertEquals(1, users.size());
        assertEquals("admin@ats.local", users.get(0).getEmail());
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
        verify(userRepository, never()).delete(any());
    }
}

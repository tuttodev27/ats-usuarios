package com.ats.user.application.service;

import com.ats.user.domain.exception.MenuAlreadyExistsException;
import com.ats.user.domain.exception.MenuNotFoundException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Menu;
import com.ats.user.domain.model.Module;
import com.ats.user.domain.port.out.MenuRepositoryPort;
import com.ats.user.domain.port.out.ModuleRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuRepositoryPort menuRepository;

    @Mock
    private ModuleRepositoryPort moduleRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    void createShouldSaveMenuWhenPathIsAvailableAndModuleExists() {
        Menu input = menuInput("/dashboard");
        when(menuRepository.existsByPathIgnoreCase("/dashboard")).thenReturn(false);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(moduleExisting(1L)));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Menu created = menuService.create(input);

        assertEquals("/dashboard", created.getPath());
        assertEquals("ATS_DASHBOARD_VIEW", created.getRequiredPermissionCode());
        assertNotNull(created.getCreatedAt());
        verify(menuRepository).save(input);
    }

    @Test
    void createShouldFailWhenPathAlreadyExists() {
        when(menuRepository.existsByPathIgnoreCase("/dashboard")).thenReturn(true);

        assertThrows(MenuAlreadyExistsException.class, () -> menuService.create(menuInput("/dashboard")));
        verify(menuRepository, never()).save(any());
    }

    @Test
    void createShouldFailWhenModuleDoesNotExist() {
        when(menuRepository.existsByPathIgnoreCase("/dashboard")).thenReturn(false);
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ModuleNotFoundException.class, () -> menuService.create(menuInput("/dashboard")));
        verify(menuRepository, never()).save(any());
    }

    @Test
    void listShouldReturnRepositoryData() {
        when(menuRepository.findAll()).thenReturn(List.of(menuExisting(1L, "/dashboard")));

        List<Menu> menus = menuService.list();

        assertEquals(1, menus.size());
        assertEquals("/dashboard", menus.getFirst().getPath());
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(menuRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MenuNotFoundException.class, () -> menuService.getById(99L));
    }

    @Test
    void updateShouldModifyFields() {
        Menu current = menuExisting(1L, "/dashboard");
        Menu updateInput = Menu.builder()
                .title("Usuarios")
                .path("/users")
                .moduleId(2L)
                .orderIndex(2)
                .requiredPermissionCode("user_read")
                .active(false)
                .build();

        when(menuRepository.findById(1L)).thenReturn(Optional.of(current));
        when(menuRepository.findByPathIgnoreCase("/users")).thenReturn(Optional.empty());
        when(moduleRepository.findById(2L)).thenReturn(Optional.of(moduleExisting(2L)));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Menu updated = menuService.update(1L, updateInput);

        assertEquals("Usuarios", updated.getTitle());
        assertEquals("/users", updated.getPath());
        assertEquals(2L, updated.getModuleId());
        assertEquals("USER_READ", updated.getRequiredPermissionCode());
        assertFalse(updated.isActive());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateShouldFailWhenPathBelongsToAnotherMenu() {
        Menu current = menuExisting(1L, "/dashboard");
        Menu another = menuExisting(2L, "/users");

        when(menuRepository.findById(1L)).thenReturn(Optional.of(current));
        when(menuRepository.findByPathIgnoreCase("/users")).thenReturn(Optional.of(another));

        assertThrows(MenuAlreadyExistsException.class, () -> menuService.update(1L, Menu.builder()
                .title("Usuarios")
                .path("/users")
                .moduleId(1L)
                .active(true)
                .build()));
    }

    @Test
    void deleteShouldSetMenuInactive() {
        Menu current = menuExisting(1L, "/dashboard");
        when(menuRepository.findById(1L)).thenReturn(Optional.of(current));
        when(menuRepository.save(any(Menu.class))).thenAnswer(invocation -> invocation.getArgument(0));

        menuService.delete(1L);

        assertFalse(current.isActive());
        assertNotNull(current.getUpdatedAt());
    }

    private Menu menuInput(String path) {
        return Menu.builder()
                .title("Dashboard")
                .path(path)
                .moduleId(1L)
                .orderIndex(1)
                .requiredPermissionCode("ats_dashboard_view")
                .active(true)
                .build();
    }

    private Menu menuExisting(Long id, String path) {
        return Menu.builder()
                .id(id)
                .title("Dashboard")
                .path(path)
                .moduleId(1L)
                .orderIndex(1)
                .requiredPermissionCode("ATS_DASHBOARD_VIEW")
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    private Module moduleExisting(Long id) {
        return Module.builder()
                .id(id)
                .code("ATS")
                .name("ATS Core")
                .active(true)
                .build();
    }
}

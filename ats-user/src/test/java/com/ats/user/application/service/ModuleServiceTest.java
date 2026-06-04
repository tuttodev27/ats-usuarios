package com.ats.user.application.service;

import com.ats.user.domain.exception.ModuleAlreadyExistsException;
import com.ats.user.domain.exception.ModuleNotFoundException;
import com.ats.user.domain.model.Module;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModuleServiceTest {

    @Mock
    private ModuleRepositoryPort moduleRepository;

    @InjectMocks
    private ModuleService moduleService;

    @Test
    void createModuleShouldSaveWhenCodeIsAvailable() {
        Module input = moduleInput(" ats-request ", "ATS Request");
        when(moduleRepository.existsByCodeIgnoreCase("ATS-REQUEST")).thenReturn(false);
        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Module created = moduleService.createModule(input);

        assertEquals("ATS-REQUEST", created.getCode());
        assertEquals("ATS Request", created.getName());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        verify(moduleRepository).save(input);
    }

    @Test
    void createModuleShouldFailWhenCodeAlreadyExists() {
        when(moduleRepository.existsByCodeIgnoreCase("ATS")).thenReturn(true);

        assertThrows(ModuleAlreadyExistsException.class,
                () -> moduleService.createModule(moduleInput("ats", "ATS Core")));
        verify(moduleRepository, never()).save(any());
    }

    @Test
    void getModulesShouldReturnRepositoryData() {
        when(moduleRepository.findAll()).thenReturn(List.of(moduleExisting(1L, "ATS")));

        List<Module> modules = moduleService.getModules();

        assertEquals(1, modules.size());
        assertEquals("ATS", modules.getFirst().getCode());
    }

    @Test
    void getModuleByIdShouldThrowWhenMissing() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ModuleNotFoundException.class, () -> moduleService.getModuleById(99L));
    }

    @Test
    void updateModuleShouldModifyFields() {
        Module current = moduleExisting(1L, "ATS");
        Module updateInput = Module.builder()
                .code("ats-request")
                .name("ATS Request")
                .description("Nuevo modulo")
                .active(false)
                .build();

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(moduleRepository.findByCodeIgnoreCase("ATS-REQUEST")).thenReturn(Optional.empty());
        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Module updated = moduleService.updateModule(1L, updateInput);

        assertEquals("ATS-REQUEST", updated.getCode());
        assertEquals("ATS Request", updated.getName());
        assertEquals("Nuevo modulo", updated.getDescription());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    void updateModuleShouldFailWhenCodeBelongsToAnotherModule() {
        Module current = moduleExisting(1L, "ATS");
        Module another = moduleExisting(2L, "ATS-REQUEST");
        Module updateInput = Module.builder()
                .code("ats-request")
                .name("ATS Request")
                .active(true)
                .build();

        when(moduleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(moduleRepository.findByCodeIgnoreCase("ATS-REQUEST")).thenReturn(Optional.of(another));

        assertThrows(ModuleAlreadyExistsException.class, () -> moduleService.updateModule(1L, updateInput));
        verify(moduleRepository, never()).save(any());
    }

    @Test
    void updateModuleStatusShouldToggleActive() {
        Module current = moduleExisting(1L, "ATS");
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Module updated = moduleService.updateModuleStatus(1L, false);
        assertFalse(updated.isActive());
        assertNotNull(updated.getUpdatedAt());

        updated = moduleService.updateModuleStatus(1L, true);
        assertTrue(updated.isActive());
    }

    @Test
    void updateModuleStatusShouldThrowWhenModuleNotFound() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ModuleNotFoundException.class, () -> moduleService.updateModuleStatus(99L, false));
    }

    @Test
    void deleteModuleShouldSetInactive() {
        Module current = moduleExisting(1L, "ATS");
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(current));
        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moduleService.deleteModuleById(1L);

        assertFalse(current.isActive());
        assertNotNull(current.getUpdatedAt());
        verify(moduleRepository).save(current);
    }

    @Test
    void deleteModuleShouldThrowWhenMissing() {
        when(moduleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ModuleNotFoundException.class, () -> moduleService.deleteModuleById(99L));
        verify(moduleRepository, never()).save(any());
    }

    private Module moduleInput(String code, String name) {
        return Module.builder()
                .code(code)
                .name(name)
                .description("desc")
                .active(true)
                .build();
    }

    private Module moduleExisting(Long id, String code) {
        return Module.builder()
                .id(id)
                .code(code)
                .name("Modulo " + code)
                .description("desc")
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}

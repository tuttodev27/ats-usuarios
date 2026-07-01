package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.domain.model.Menu;
import com.ats.user.infrastructure.adapter.out.persistence.entity.MenuEntity;
import com.ats.user.infrastructure.adapter.out.persistence.entity.ModuleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.MenuMapper;
import com.ats.user.infrastructure.adapter.out.persistence.repository.MenuJpaRepository;
import com.ats.user.infrastructure.adapter.out.persistence.repository.ModuleJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuRepositoryAdapterTest {

    @Mock
    private MenuJpaRepository menuJpaRepository;

    @Mock
    private ModuleJpaRepository moduleJpaRepository;

    @Mock
    private MenuMapper menuMapper;

    @InjectMocks
    private MenuRepositoryAdapter menuRepositoryAdapter;

    @Test
    void saveShouldPersistNewMenu() {
        var moduleEntity = moduleEntity(10L);
        var menu = domainMenu(null, 10L);
        var savedEntity = menuEntity(1L, moduleEntity);
        var savedDomain = domainMenu(1L, 10L);

        when(moduleJpaRepository.findById(10L)).thenReturn(Optional.of(moduleEntity));
        when(menuJpaRepository.save(any(MenuEntity.class))).thenReturn(savedEntity);
        when(menuMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = menuRepositoryAdapter.save(menu);

        assertEquals(1L, result.getId());
        verify(moduleJpaRepository).findById(10L);
        verify(menuJpaRepository).save(any(MenuEntity.class));
        verify(menuMapper).toDomain(savedEntity);
    }

    @Test
    void saveShouldUpdateExistingMenu() {
        var moduleEntity = moduleEntity(10L);
        var existingEntity = menuEntity(1L, moduleEntity);
        var menu = domainMenu(1L, 10L);
        var savedDomain = domainMenu(1L, 10L);

        when(menuJpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(moduleJpaRepository.findById(10L)).thenReturn(Optional.of(moduleEntity));
        when(menuJpaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(menuMapper.toDomain(existingEntity)).thenReturn(savedDomain);

        var result = menuRepositoryAdapter.save(menu);

        assertEquals(1L, result.getId());
        verify(menuJpaRepository).findById(1L);
        verify(menuJpaRepository).save(existingEntity);
    }

    @Test
    void findByIdShouldReturnMenuWhenFound() {
        var moduleEntity = moduleEntity(10L);
        var entity = menuEntity(1L, moduleEntity);
        var domain = domainMenu(1L, 10L);

        when(menuJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(menuMapper.toDomain(entity)).thenReturn(domain);

        var result = menuRepositoryAdapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(menuJpaRepository.findById(99L)).thenReturn(Optional.empty());

        var result = menuRepositoryAdapter.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllShouldReturnAllMenusMapped() {
        var moduleEntity = moduleEntity(10L);
        var entities = List.of(menuEntity(1L, moduleEntity), menuEntity(2L, moduleEntity));
        var domains = List.of(domainMenu(1L, 10L), domainMenu(2L, 10L));

        when(menuJpaRepository.findAllByOrderByIdAsc()).thenReturn(entities);
        when(menuMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(menuMapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        var result = menuRepositoryAdapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void existsByPathIgnoreCaseShouldDelegate() {
        when(menuJpaRepository.existsByPathIgnoreCase("/test")).thenReturn(true);

        var result = menuRepositoryAdapter.existsByPathIgnoreCase("/test");

        assertTrue(result);
    }

    @Test
    void findByPathIgnoreCaseShouldReturnMenuWhenFound() {
        var moduleEntity = moduleEntity(10L);
        var entity = menuEntity(1L, moduleEntity);
        var domain = domainMenu(1L, 10L);

        when(menuJpaRepository.findByPathIgnoreCase("/test")).thenReturn(Optional.of(entity));
        when(menuMapper.toDomain(entity)).thenReturn(domain);

        var result = menuRepositoryAdapter.findByPathIgnoreCase("/test");

        assertTrue(result.isPresent());
    }

    @Test
    void findByPathIgnoreCaseShouldReturnEmptyWhenNotFound() {
        when(menuJpaRepository.findByPathIgnoreCase("/unknown")).thenReturn(Optional.empty());

        var result = menuRepositoryAdapter.findByPathIgnoreCase("/unknown");

        assertTrue(result.isEmpty());
    }

    private ModuleEntity moduleEntity(Long id) {
        return ModuleEntity.builder()
                .id(id)
                .code("TEST")
                .name("Test")
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private MenuEntity menuEntity(Long id, ModuleEntity module) {
        return MenuEntity.builder()
                .id(id)
                .title("Test Menu")
                .path("/test")
                .orderIndex(1)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .module(module)
                .build();
    }

    private Menu domainMenu(Long id, Long moduleId) {
        return Menu.builder()
                .id(id)
                .title("Test Menu")
                .path("/test")
                .moduleId(moduleId)
                .orderIndex(1)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.domain.model.Module;
import com.ats.user.infrastructure.adapter.out.persistence.entity.ModuleEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.ModuleMapper;
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
class ModuleRepositoryAdapterTest {

    @Mock
    private ModuleJpaRepository moduleJpaRepository;

    @Mock
    private ModuleMapper moduleMapper;

    @InjectMocks
    private ModuleRepositoryAdapter moduleRepositoryAdapter;

    @Test
    void saveShouldPersistNewModule() {
        var domain = domainModule(null, "NEW");
        var savedEntity = moduleEntity(1L, "NEW");
        var savedDomain = domainModule(1L, "NEW");

        when(moduleJpaRepository.save(any(ModuleEntity.class))).thenReturn(savedEntity);
        when(moduleMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = moduleRepositoryAdapter.save(domain);

        assertEquals(1L, result.getId());
        assertEquals("NEW", result.getCode());
        verify(moduleJpaRepository).save(any(ModuleEntity.class));
    }

    @Test
    void saveShouldUpdateExistingModule() {
        var existingEntity = moduleEntity(1L, "OLD");
        var domain = domainModule(1L, "UPDATED");

        when(moduleJpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(moduleJpaRepository.save(existingEntity)).thenReturn(existingEntity);
        when(moduleMapper.toDomain(existingEntity)).thenReturn(domain);

        var result = moduleRepositoryAdapter.save(domain);

        assertEquals(1L, result.getId());
        assertEquals("UPDATED", result.getCode());
        verify(moduleJpaRepository).findById(1L);
    }

    @Test
    void findByIdShouldReturnModuleWhenFound() {
        var entity = moduleEntity(1L, "TEST");
        var domain = domainModule(1L, "TEST");

        when(moduleJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(moduleMapper.toDomain(entity)).thenReturn(domain);

        var result = moduleRepositoryAdapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("TEST", result.get().getCode());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(moduleJpaRepository.findById(99L)).thenReturn(Optional.empty());

        var result = moduleRepositoryAdapter.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllShouldReturnAllModulesMapped() {
        var entities = List.of(moduleEntity(1L, "A"), moduleEntity(2L, "B"));
        var domains = List.of(domainModule(1L, "A"), domainModule(2L, "B"));

        when(moduleJpaRepository.findAllByOrderByIdAsc()).thenReturn(entities);
        when(moduleMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(moduleMapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        var result = moduleRepositoryAdapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void existsByCodeIgnoreCaseShouldDelegate() {
        when(moduleJpaRepository.existsByCodeIgnoreCase("TEST")).thenReturn(true);

        var result = moduleRepositoryAdapter.existsByCodeIgnoreCase("TEST");

        assertTrue(result);
    }

    @Test
    void findByCodeIgnoreCaseShouldReturnModuleWhenFound() {
        var entity = moduleEntity(1L, "TEST");
        var domain = domainModule(1L, "TEST");

        when(moduleJpaRepository.findByCodeIgnoreCase("TEST")).thenReturn(Optional.of(entity));
        when(moduleMapper.toDomain(entity)).thenReturn(domain);

        var result = moduleRepositoryAdapter.findByCodeIgnoreCase("TEST");

        assertTrue(result.isPresent());
    }

    @Test
    void findByCodeIgnoreCaseShouldReturnEmptyWhenNotFound() {
        when(moduleJpaRepository.findByCodeIgnoreCase("UNKNOWN")).thenReturn(Optional.empty());

        var result = moduleRepositoryAdapter.findByCodeIgnoreCase("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    private ModuleEntity moduleEntity(Long id, String code) {
        return ModuleEntity.builder()
                .id(id)
                .code(code)
                .name(code)
                .description("desc")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }

    private Module domainModule(Long id, String code) {
        return Module.builder()
                .id(id)
                .code(code)
                .name(code)
                .description("desc")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .createdBy(1L)
                .updatedBy(1L)
                .build();
    }
}

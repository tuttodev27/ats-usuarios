package com.ats.user.infrastructure.adapter.out.persistence.adapter;

import com.ats.user.domain.model.User;
import com.ats.user.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.ats.user.infrastructure.adapter.out.persistence.mapper.UserMapper;
import com.ats.user.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void saveShouldPersistUser() {
        var domain = domainUser(null, "test@test.com");
        var entity = userEntity(null, "test@test.com");
        var savedEntity = userEntity(1L, "test@test.com");
        var savedDomain = domainUser(1L, "test@test.com");

        when(userMapper.toEntity(domain)).thenReturn(entity);
        when(userJpaRepository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        var result = userRepositoryAdapter.save(domain);

        assertEquals(1L, result.getId());
        assertEquals("test@test.com", result.getEmail());
        verify(userJpaRepository).save(entity);
    }

    @Test
    void findByIdShouldReturnUserWhenFound() {
        var entity = userEntity(1L, "test@test.com");
        var domain = domainUser(1L, "test@test.com");

        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(domain);

        var result = userRepositoryAdapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getEmail());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(userJpaRepository.findById(99L)).thenReturn(Optional.empty());

        var result = userRepositoryAdapter.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByEmailShouldReturnUserWhenFound() {
        var entity = userEntity(1L, "test@test.com");
        var domain = domainUser(1L, "test@test.com");

        when(userJpaRepository.findByEmail("test@test.com")).thenReturn(Optional.of(entity));
        when(userMapper.toDomain(entity)).thenReturn(domain);

        var result = userRepositoryAdapter.findByEmail("test@test.com");

        assertTrue(result.isPresent());
    }

    @Test
    void findByEmailShouldReturnEmptyWhenNotFound() {
        when(userJpaRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        var result = userRepositoryAdapter.findByEmail("missing@test.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmailShouldDelegate() {
        when(userJpaRepository.existsByEmail("test@test.com")).thenReturn(true);

        var result = userRepositoryAdapter.existsByEmail("test@test.com");

        assertTrue(result);
    }

    @Test
    void existsByIdShouldDelegate() {
        when(userJpaRepository.existsById(1L)).thenReturn(true);

        var result = userRepositoryAdapter.existsById(1L);

        assertTrue(result);
    }

    @Test
    void findAllShouldReturnAllUsersMapped() {
        var entities = List.of(userEntity(1L, "a@test.com"), userEntity(2L, "b@test.com"));
        var domains = List.of(domainUser(1L, "a@test.com"), domainUser(2L, "b@test.com"));

        when(userJpaRepository.findAll()).thenReturn(entities);
        when(userMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));
        when(userMapper.toDomain(entities.get(1))).thenReturn(domains.get(1));

        var result = userRepositoryAdapter.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAllByActiveShouldDelegate() {
        var entities = List.of(userEntity(1L, "a@test.com"));
        var domains = List.of(domainUser(1L, "a@test.com"));

        when(userJpaRepository.findAllByActive(true)).thenReturn(entities);
        when(userMapper.toDomain(entities.get(0))).thenReturn(domains.get(0));

        var result = userRepositoryAdapter.findAllByActive(true);

        assertEquals(1, result.size());
    }

    @Test
    void deleteShouldSetActiveFalseAndSave() {
        var existingEntity = userEntity(1L, "test@test.com");
        existingEntity.setActive(true);
        var updatedEntity = userEntity(1L, "test@test.com");
        updatedEntity.setActive(false);
        var domain = domainUser(1L, "test@test.com");
        domain.setActive(false);

        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity));
        when(userJpaRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(userMapper.toDomain(updatedEntity)).thenReturn(domain);

        var result = userRepositoryAdapter.delete(1L);

        assertFalse(result.getActive());
        verify(userJpaRepository).findById(1L);
        verify(userJpaRepository).save(existingEntity);
    }

    private UserEntity userEntity(Long id, String email) {
        return UserEntity.builder()
                .id(id)
                .name("Test")
                .lastName("User")
                .email(email)
                .passwordHash("hash")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User domainUser(Long id, String email) {
        return User.builder()
                .id(id)
                .name("Test")
                .lastName("User")
                .email(email)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

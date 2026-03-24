package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.out.UserRepositoryPort;
import com.ats.user.infrastructure.out.mapper.UserMapper;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(user);
        var save = userJpaRepository.save(entity);
        return userMapper.toDomain(save);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllByActive(boolean active) {
        return userJpaRepository.findAllByActive(active).stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public void delete(Long id) {
        var entity= userJpaRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found: " + id));
        entity.setActive(false);
        userJpaRepository.save(entity);
    }
}

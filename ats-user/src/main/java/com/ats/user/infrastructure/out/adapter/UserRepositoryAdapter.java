package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.model.User;
import com.ats.user.domain.port.out.UserRepositoryPort;
import com.ats.user.infrastructure.out.mapper.UserMapper;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

}

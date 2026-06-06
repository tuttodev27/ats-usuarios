package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.Page;
import com.ats.user.domain.model.PageQuery;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.out.UserRepositoryPort;
import com.ats.user.infrastructure.out.mapper.UserMapper;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import com.ats.user.infrastructure.out.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Page<User> searchUsers(String search, Boolean active, PageQuery pageQuery) {
        var spec = UserSpecification.withFilters(search, active);
        var pageable = PageRequest.of(pageQuery.page(), pageQuery.size());
        var springPage = userJpaRepository.findAll(spec, pageable);
        var content = springPage.getContent().stream()
                .map(userMapper::toDomain)
                .toList();
        return new Page<>(content, springPage.getNumber(), springPage.getSize(), springPage.getTotalElements());
    }

    @Override
    public boolean existsById(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public User delete(Long id) {
        var entity= userJpaRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found: " + id));
        entity.setActive(false);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }
}

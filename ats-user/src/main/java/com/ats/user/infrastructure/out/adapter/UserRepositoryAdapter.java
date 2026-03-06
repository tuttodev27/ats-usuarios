package com.ats.user.infrastructure.out.adapter;

import com.ats.user.domain.exception.RoleNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.out.UserRepositoryPort;
import com.ats.user.infrastructure.out.entity.RoleEntity;
import com.ats.user.infrastructure.out.mapper.UserMapper;
import com.ats.user.infrastructure.out.repository.RoleJpaRepository;
import com.ats.user.infrastructure.out.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(user);
        entity.setRoles(resolveActiveRoles(user.getRoles()));
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
    public List<User> findAllActive() {
        return userJpaRepository.findAllByActiveTrue().stream()
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

    private Set<RoleEntity> resolveActiveRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(this::resolveRole)
                .collect(Collectors.toSet());
    }

    private RoleEntity resolveRole(Role role) {
        if (role.getId() != null) {
            return roleJpaRepository.findById(role.getId())
                    .filter(r -> Boolean.TRUE.equals(r.getActive()))
                    .orElseThrow(() -> new RoleNotFoundException("Active role not found for id: " + role.getId()));
        }
        if (role.getName() != null && !role.getName().isBlank()) {
            return roleJpaRepository.findByNameIgnoreCaseAndActiveTrue(role.getName().trim())
                    .orElseThrow(() -> new RoleNotFoundException("Active role not found: " + role.getName()));
        }
        throw new RoleNotFoundException("Role is required");
    }

}

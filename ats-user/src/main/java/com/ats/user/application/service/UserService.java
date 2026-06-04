package com.ats.user.application.service;

import com.ats.user.application.service.password.PasswordPolicy;
import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.exception.RoleNotAvailableException;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.Role;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.domain.port.out.PasswordHasherPort;
import com.ats.user.domain.port.out.RoleRepositoryPort;
import com.ats.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class UserService implements UserUseCase {

    private final UserRepositoryPort userRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordHasherPort passwordHasher;
    private final PasswordPolicy passwordPolicy;

    public UserService(UserRepositoryPort userRepository,
                       RoleRepositoryPort roleRepository,
                       PasswordHasherPort passwordHasher,
                       PasswordPolicy passwordPolicy) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
        this.passwordPolicy = passwordPolicy;
    }

    @Override
    public User create(User user, Long roleId, String rawPassword) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already registered: " + user.getEmail());
        }
        passwordPolicy.validate(rawPassword);
        var role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotAvailableException("Role not found: " + roleId));
        if (!role.isActive()) {
            throw new RoleNotAvailableException("Role is not active: " + roleId);
        }

        user.setRoles(Set.of(role));
        user.setPasswordHash(passwordHasher.encode(rawPassword));
        if (user.getActive() == null) {
            user.setActive(true);
        }
        return userRepository.save(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found: " + id));
    }

    @Override
    public List<User> listUsers(Boolean active) {
        if (active == null) {
            return userRepository.findAll();
        }
        return userRepository.findAllByActive(active);
    }

    @Override
    public List<Role> listAvailableRoles() {
        return roleRepository.findAllActive();
    }

    @Override
    public User update(Long id, User user) {
        User currentUser= userRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("User not found: " + id));
        currentUser.setName(user.getName());
        currentUser.setLastName(user.getLastName());
        currentUser.setCountryCode(user.getCountryCode());
        currentUser.setPhone(user.getPhone());
        if(user.getActive() !=null){
            currentUser.setActive(user.getActive());
        }
        currentUser.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(currentUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found: " + id);
        }
        userRepository.delete(id);
    }
}

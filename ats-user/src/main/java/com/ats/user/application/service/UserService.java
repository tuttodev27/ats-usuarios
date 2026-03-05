package com.ats.user.application.service;

import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.exception.UserNotFoundException;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.UserUseCase;
import com.ats.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService implements UserUseCase {

    public final UserRepositoryPort userRepository;

    public UserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already registered: " + user.getEmail());
        }
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
    public List<User> listActive() {
        return userRepository.findAllActive();
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

package com.ats.user.application.service;

import com.ats.user.domain.exception.EmailAlreadyExistException;
import com.ats.user.domain.model.User;
import com.ats.user.domain.port.in.CreateUserUseCase;
import com.ats.user.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateUserService implements CreateUserUseCase {

    public final UserRepositoryPort userRepository;

    public CreateUserService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException("Email already registered: " + user.getEmail());
        }
        return userRepository.save(user);
    }


}

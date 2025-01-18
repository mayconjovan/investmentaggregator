package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.CreateUserDTO;
import com.mjp.investmentaggregator.entities.User;
import com.mjp.investmentaggregator.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UUID createUser(CreateUserDTO dto) {
        User user = new User(null, dto.username(), dto.email(), dto.password(), Instant.now(), null);
        var userSaved = userRepository.save(user);

        return userSaved.getUserId();

    }
}

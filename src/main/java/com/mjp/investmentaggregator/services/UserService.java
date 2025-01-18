package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.CreateUserDTO;
import com.mjp.investmentaggregator.dtos.UpdateUserDTO;
import com.mjp.investmentaggregator.entities.User;
import com.mjp.investmentaggregator.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

    public Optional<User> getUserByid(String userId){
        return userRepository.findById(UUID.fromString(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteById(String userId) {
        var id = UUID.fromString(userId);

        var userExists = userRepository.existsById(id);

        if(userExists) {
            userRepository.deleteById(id);
        }
    }

    public void updateUserById(String userId, UpdateUserDTO dto){
        var id = UUID.fromString(userId);
        var userEntity = userRepository.findById(id);

        if (userEntity.isPresent()) {
            var user = userEntity.get();
            if (dto.username() != null) {
                user.setUsername(dto.username());
            }

            if (dto.password() != null) {
                user.setPassword(dto.password());
            }

            userRepository.save(user);
        }



    }
}

package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.AccountResponseDTO;
import com.mjp.investmentaggregator.dtos.CreateAccountDTO;
import com.mjp.investmentaggregator.dtos.CreateUserDTO;
import com.mjp.investmentaggregator.dtos.UpdateUserDTO;
import com.mjp.investmentaggregator.entities.Account;
import com.mjp.investmentaggregator.entities.BillingAddress;
import com.mjp.investmentaggregator.entities.User;
import com.mjp.investmentaggregator.repositories.AccountRepository;
import com.mjp.investmentaggregator.repositories.BillingAddressRepository;
import com.mjp.investmentaggregator.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BillingAddressRepository billingAddressRepository;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, BillingAddressRepository billingAddressRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.billingAddressRepository = billingAddressRepository;
    }

    public UUID createUser(CreateUserDTO dto) {
        User user = new User(null, dto.username(), dto.email(), dto.password(), Instant.now(), null);
        var userSaved = userRepository.save(user);

        return userSaved.getUserId();

    }

    public Optional<User> getUserByid(String userId) {
        return userRepository.findById(UUID.fromString(userId));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteById(String userId) {
        var id = UUID.fromString(userId);

        var userExists = userRepository.existsById(id);

        if (userExists) {
            userRepository.deleteById(id);
        }
    }

    public void updateUserById(String userId, UpdateUserDTO dto) {
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

    public void createAccount(String userId, CreateAccountDTO createAccountDTO) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var account = new Account(
                null,
                createAccountDTO.description(),
                user,
                null,
                new ArrayList<>()
        );

        accountRepository.save(account);

        var billingAddress = new BillingAddress(
                null,
                account,
                createAccountDTO.street(),
                createAccountDTO.number()
        );

        billingAddressRepository.save(billingAddress);
    }


    public List<AccountResponseDTO> listAccounts(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return user.getAccountList().stream()
                .map(ac ->
                        new AccountResponseDTO(ac.getAccountId().toString(), ac.getDescription()))
                .toList();


    }
}

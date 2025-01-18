package com.mjp.investmentaggregator.services;

import com.mjp.investmentaggregator.dtos.CreateUserDTO;
import com.mjp.investmentaggregator.dtos.UpdateUserDTO;
import com.mjp.investmentaggregator.entities.User;
import com.mjp.investmentaggregator.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Captor
    private ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Nested
    class CreateUser {

        @Test
        @DisplayName("Should create a user with success")
        void shouldCreateUser() {

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            var input = new CreateUserDTO("username", "email@email.com", "123");

            var output = userService.createUser(input);

            assertNotNull(output);
            var userCaptured = userArgumentCaptor.getValue();

            assertEquals(input.username(), userCaptured.getUsername());
            assertEquals(input.email(), userCaptured.getEmail());
            assertEquals(input.password(), userCaptured.getPassword());


        }

        @Test
        @DisplayName("Should throw exception when error occurs")
        void shouldThrowExceptionWhenErrorOccurs() {

            doThrow(new RuntimeException()).when(userRepository).save(any());
            var input = new CreateUserDTO("username", "email@email.com", "123");

            assertThrows(RuntimeException.class, () -> userService.createUser(input));

        }

    }

    @Nested
    class GetUserById {
        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldGetUserByIdWithSuccessWhenOptionalIsPresent() {
            //Arrange
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());


            //Act

            Optional<User> output = userService.getUserByid(user.getUserId().toString());
            //Assertions

            assertTrue(output.isPresent());
            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("Should get user by id with success when optional is present")
        void shouldNotGetUserByIdWithSuccessWhenOptionalIsEmpty() {
            //Arrange
            var userId = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());


            //Act

            Optional<User> output = userService.getUserByid(userId.toString());
            //Assertions

            assertTrue(output.isEmpty());
            assertEquals(userId, uuidArgumentCaptor.getValue());
        }
    }

    @Nested
    class ListUsers {
        @Test
        @DisplayName("Should return all users with success")
        void shouldReturnAllUsersWithSuccess() {
            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            var userList = List.of(user);

            doReturn(userList).when(userRepository).findAll();

            List<User> output = userService.getAllUsers();

            assertNotNull(output);
            assertEquals(userList.size(), output.size());


        }
    }

    @Nested
    class DeleteById {
        @Test
        @DisplayName("Should delete user with success when user exists")
        void shouldDeleteUserWithSuccessWhenUserExists() {


            doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
            doNothing().when(userRepository).deleteById(uuidArgumentCaptor.capture());


            var userId = UUID.randomUUID();
            userService.deleteById(userId.toString());

            var idList = uuidArgumentCaptor.getAllValues();
            assertEquals(userId, idList.get(0));
            assertEquals(userId, idList.get(1));
            verify(userRepository, times(1)).existsById(idList.get(0));
            verify(userRepository, times(1)).deleteById(idList.get(1));

        }

        @Test
        @DisplayName("Shouldn't delete user when user doesn't exists")
        void shouldNotDeleteUserWhenUserNotExists() {

            doReturn(false).when(userRepository).existsById(uuidArgumentCaptor.capture());
            var userId = UUID.randomUUID();


            userService.deleteById(userId.toString());

            assertEquals(userId, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).existsById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).deleteById(any());

        }


    }

    @Nested
    class UpdateUSerById {
        @Test
        @DisplayName("Should update user by id when user existes and username and password is filled")
        void shouldUpdateUserByIdWhenUserExistsAndUsernameAndPasswordIsFilled() {
            //Arrange
            var updateUserDto = new UpdateUserDTO(
                    "newusername",
                    "newpassword"
            );

            var user = new User(
                    UUID.randomUUID(),
                    "username",
                    "email@email.com",
                    "password",
                    Instant.now(),
                    null);

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());


            //Act
            userService.updateUserById(user.getUserId().toString(), updateUserDto);
            //Assertions

            assertEquals(user.getUserId(), uuidArgumentCaptor.getValue());
            var userCapture = userArgumentCaptor.getValue();

            assertEquals(user.getUsername(), userCapture.getUsername());
            assertEquals(user.getPassword(), userCapture.getPassword());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("Should not update user when user not exists")
        void shouldNotUpdateUserWhenUserNotExists() {

            var updateUserDto = new UpdateUserDTO(
                    "newusername",
                    "newpassword"
            );
            var userId = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            userService.updateUserById(userId.toString(), updateUserDto);

            assertEquals(userId, uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).save(any());
        }
    }
}
package com.disneymovie.disneyJava.user.service;

import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.user.dto.LoginRequestDto;
import com.disneymovie.disneyJava.user.dto.RegisterRequestDto;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import com.disneymovie.disneyJava.user.repository.UserRepository;
import com.disneymovie.disneyJava.user.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class UserServiceTest {
    AutoCloseable openMocks;
    private UserService userService;

    @Mock
    UserRepository userRepository;
    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        userService = new UserService(userRepository);
    }

    @Test
    void loginTestOk() throws ElementDoesNotExistException {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email", "admin");
        User userMock = new User(1, UserRole.admin, "email", "admin");
        when(userRepository.getByEmail("email", "admin")).thenReturn(userMock);
        User response = userService.login(loginRequestDto);

        Assertions.assertEquals(response, userMock);
        verify(userRepository, times(1)).getByEmail("email", "admin");
    }

    @Test
    void loginTestElementDoesNotExistException() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email", "admin");
        when(userRepository.getByEmail("email", "admin")).thenReturn(null);

        Assertions.assertThrows(ElementDoesNotExistException.class, () -> userService.login(loginRequestDto));
    }

    @Test
    void registerTest() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(1, "email", "admin");
        when(userRepository.register(1,"email","admin")).thenReturn(1);

        Integer response = userService.register(registerRequestDto);
        Assertions.assertEquals(1, response);
    }

    @Test
    void findByIdTestOk() {
        User userMock = new User(1,UserRole.admin,"email","admin");
        when(userRepository.findById(1)).thenReturn(Optional.of(userMock));
        Assertions.assertEquals(userMock, userService.findById(1).get());
    }

    @Test
    void findByIdTestEmpty() {
        User userMock = new User(1,UserRole.admin,"email","admin");
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Assertions.assertEquals(Optional.empty(), userService.findById(1));
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

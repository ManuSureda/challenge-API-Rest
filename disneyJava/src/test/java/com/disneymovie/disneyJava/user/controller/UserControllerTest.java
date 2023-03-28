package com.disneymovie.disneyJava.user.controller;

import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.exceptions.InvalidLoginException;
import com.disneymovie.disneyJava.exceptions.UserAllReadyExistException;
import com.disneymovie.disneyJava.user.dto.LoginRequestDto;
import com.disneymovie.disneyJava.user.dto.RegisterRequestDto;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import com.disneymovie.disneyJava.user.services.UserService;
import com.disneymovie.disneyJava.user.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;

import javax.xml.bind.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserControllerTest {
    AutoCloseable openMocks;
    private UserController userController;

    @Mock
    SessionManager sessionManager;
    @Mock
    UserService userService;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        userController = new UserController(sessionManager,userService);
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Authorization", token);
        return responseHeaders;
    }

    @Test
    void loginTestOk() throws ElementDoesNotExistException, ValidationException, InvalidLoginException {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email", "admin");
        User userMock = new User(1, UserRole.admin, "email", "admin");
        when(userService.login(loginRequestDto)).thenReturn(userMock);
        String token = sessionManager.createSession(userMock);

        ResponseEntity<?> response = userController.login(loginRequestDto);
        ResponseEntity<?> responseEntity = ResponseEntity.ok().headers(createHeaders(token)).build();

        assertNotNull(response);
        assertEquals(responseEntity.getStatusCode(), response.getStatusCode());
    }

    @Test
    void loginTestElementDoesNotExistException() throws ElementDoesNotExistException {
        LoginRequestDto loginRequestDto = new LoginRequestDto("email", "admin");

        doThrow(new ElementDoesNotExistException("Email or password are wrong"))
                .when(userService).login(loginRequestDto);

        Assertions.assertThrows(InvalidLoginException.class, () -> userController.login(loginRequestDto));
    }

    @Test
    void loginTestValidationException() {
        LoginRequestDto loginRequestDto = new LoginRequestDto();

        Assertions.assertThrows(ValidationException.class, () -> userController.login(loginRequestDto));
    }

    @Test
    void registerTestOkAdmin() throws URISyntaxException, UserAllReadyExistException, DataValidationException {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(1,"email","123");
        when(userService.register(registerRequestDto)).thenReturn(1);

        User userMock = new User(1,UserRole.admin,"email","123");
        ResponseEntity<?> responseEntity = ResponseEntity.created(new URI("http://localhost:8080/auth/"+1)).body(userMock);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(responseEntity, userController.register(registerRequestDto));
        verify(userService, times(1)).register(registerRequestDto);
    }

    @Test
    void registerTestOkClient() throws URISyntaxException, UserAllReadyExistException, DataValidationException {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(2,"email","123");
        when(userService.register(registerRequestDto)).thenReturn(1);

        User userMock = new User(1,UserRole.client,"email","123");
        ResponseEntity<?> responseEntity = ResponseEntity.created(new URI("http://localhost:8080/auth/"+1)).body(userMock);

        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(responseEntity, userController.register(registerRequestDto));
        verify(userService, times(1)).register(registerRequestDto);
    }

    @Test
    void registerTestJpaSystemException() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(1,"email","123");
        doThrow(new JpaSystemException(new RuntimeException(new UserAllReadyExistException("This email " + registerRequestDto.getEmail() +" is already registered"))))
                .when(userService).register(registerRequestDto);

        assertThrows(UserAllReadyExistException.class, () -> userController.register(registerRequestDto));
    }

    @Test
    void registerTestDataValidationException() {
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(-1,"","");
        assertThrows(DataValidationException.class, () -> userController.register(registerRequestDto));
    }

    @Test
    void logoutOk() {
        User user = new User(1, UserRole.admin, "email", "123");
        String token = sessionManager.createSession(user);
        doNothing().when(sessionManager).removeSession(token);
        ResponseEntity<?> response = userController.logout(token);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void readUserByIdTestOk() throws DataValidationException, SQLException {
        User user = new User(1, UserRole.admin, "email", "123");
        when(userService.findById(1)).thenReturn(Optional.of(user));
        ResponseEntity<?> mockResponse = ResponseEntity.ok().body(user);
        ResponseEntity<?> response = userController.readUserById(1);
        Assertions.assertEquals(mockResponse, response);

        ResponseEntity<?> mockResponse_2 = ResponseEntity.notFound().build();
        when(userService.findById(1)).thenReturn(Optional.empty());
        response = userController.readUserById(10);
        Assertions.assertEquals(response, mockResponse_2);
    }

    @Test
    void readUserByIdTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException()))).when(userService).findById(1);

        Assertions.assertThrows(SQLException.class, () -> userController.readUserById(1));
    }

    @Test
    void readUserByIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> userController.readUserById(-1));
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

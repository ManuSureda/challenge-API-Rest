package com.disneymovie.disneyJava.user.controller;


import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.exceptions.InvalidLoginException;
import com.disneymovie.disneyJava.exceptions.UserAllReadyExistException;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.user.dto.LoginRequestDto;
import com.disneymovie.disneyJava.user.dto.RegisterRequestDto;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import com.disneymovie.disneyJava.user.services.UserService;
import com.disneymovie.disneyJava.user.session.SessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.ValidationException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final SessionManager sessionManager;
    private final UserService userService;

    @Autowired
    public UserController(SessionManager sessionManager, UserService userService) {
        this.sessionManager = sessionManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginRequestDto loginRequestDto) throws InvalidLoginException, ValidationException {
        if (loginRequestDto.isValid()) {
            try {
                User u = userService.login(loginRequestDto);
                String token = sessionManager.createSession(u);
                ResponseEntity<?> response = ResponseEntity.ok()
                        .headers(createHeaders(token))
                        .build();
                return response;
            } catch (ElementDoesNotExistException e) {
                throw new InvalidLoginException("Email or password are wrong");
            }
        } else {
            throw new ValidationException("username and password must have a value");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody final RegisterRequestDto registerRequestDto) throws JpaSystemException, DataValidationException, URISyntaxException, UserAllReadyExistException {

        if (registerRequestDto.isValid()) {
            try {
                Integer newID = userService.register(registerRequestDto);
                User response = new User();
                response.setIdUser(newID);
                if (registerRequestDto.getUserRoleId() == 1) {
                    response.setUserRole(UserRole.admin);
                } else {
                    response.setUserRole(UserRole.client);
                }
                response.setEmail(registerRequestDto.getEmail());
                response.setUserPassword(registerRequestDto.getPassword());
                return ResponseEntity.created(new URI("http://localhost:8080/auth/"+newID)).body(response);
            } catch (JpaSystemException e) {
                throw new UserAllReadyExistException("This email " + registerRequestDto.getEmail() +" is already registered");
            }
        } else {
            throw new DataValidationException("Wrong registration parameters");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("authorization") String token) {
        sessionManager.removeSession(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> readUserById(@PathVariable("id") Integer id) throws DataValidationException, SQLException {
        if (id > 0) {
            try {
                Optional<User> u = userService.findById(id);
                if (u.isPresent()) {
                    return ResponseEntity.ok().body(u.get());
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Id must be positive");
        }
    }

    private HttpHeaders createHeaders(String token) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("authorization", token);
        return responseHeaders;
    }


}


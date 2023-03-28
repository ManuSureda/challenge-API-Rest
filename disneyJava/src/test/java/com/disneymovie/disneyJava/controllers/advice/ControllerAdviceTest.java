package com.disneymovie.disneyJava.controllers.advice;

import com.disneymovie.disneyJava.dtos.ErrorResponseDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.exceptions.InvalidLoginException;
import com.disneymovie.disneyJava.exceptions.UserAllReadyExistException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerAdviceTest {
    private ControllerAdvice controllerAdvice;

    @BeforeEach
    void setUp() {
        controllerAdvice = new ControllerAdvice();
    }

    @Test
    void handleSQLExeptionTest() {
        SQLException e = new SQLException("Error interno en la base de datos");
        ErrorResponseDto responseDto = new ErrorResponseDto(1, "Error interno en la base de datos");

        ErrorResponseDto ans = controllerAdvice.handleSQLException(e);

        assertEquals(responseDto,ans);
    }

    @Test
    void handleURISyntaxExceptionTest() {
        URISyntaxException e = new URISyntaxException("http://localhost:8080/characters","");
        ErrorResponseDto responseDto = new ErrorResponseDto(2, "http://localhost:8080/characters");

        ErrorResponseDto ans = controllerAdvice.handleURISyntaxException(e);

        assertEquals(responseDto.getCode(),ans.getCode());
    }

    @Test
    void handleDataValidationExceptionTest() {
        DataValidationException e = new DataValidationException("Wrong data");
        ErrorResponseDto dto = new ErrorResponseDto(3, "Wrong data");

        ErrorResponseDto ans = controllerAdvice.handleDataValidationException(e);
        Assertions.assertEquals(ans, dto);
    }

    @Test
    void handleInvalidLoginExceptionTest() {
        InvalidLoginException e = new InvalidLoginException("Email or password are incorrect");
        ErrorResponseDto dto = new ErrorResponseDto(4, "Email or password are incorrect");

        ErrorResponseDto response = controllerAdvice.handleInvalidLoginException(e);
        Assertions.assertEquals(response, dto);
    }

    @Test
    void handleUserAllReadyExistExceptionTest() {
        UserAllReadyExistException e = new UserAllReadyExistException("That email is already in use");
        ErrorResponseDto dto = new ErrorResponseDto(5, "That email is already in use");

        ErrorResponseDto response = controllerAdvice.handleUserAllReadyExistException(e);
        Assertions.assertEquals(response, dto);
    }

    @Test
    void ElementDoesNotExistExceptionTest() {
        ElementDoesNotExistException e = new ElementDoesNotExistException("Element does not exist");
        ErrorResponseDto dto = new ErrorResponseDto(6, "Element does not exist");

        ErrorResponseDto response = controllerAdvice.handleElementDoesNotExistException(e);
        Assertions.assertEquals(response, dto);
    }
}

package com.disneymovie.disneyJava.controllers.advice;

import com.disneymovie.disneyJava.dtos.ErrorResponseDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.exceptions.InvalidLoginException;
import com.disneymovie.disneyJava.exceptions.UserAllReadyExistException;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URISyntaxException;
import java.sql.SQLException;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = {SQLException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleSQLException(SQLException e) {
        return new ErrorResponseDto(1, e.getMessage());
    }

    @ExceptionHandler(value = {URISyntaxException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleURISyntaxException(URISyntaxException e) {
        return new ErrorResponseDto(2, e.getMessage());
    }

    @ExceptionHandler(value = {DataValidationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDataValidationException(DataValidationException e) {
        return new ErrorResponseDto(3, e.getMessage());
    }

    @ExceptionHandler(value = {InvalidLoginException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleInvalidLoginException(InvalidLoginException e) {
        return new ErrorResponseDto(4, e.getMessage());
    }

    @ExceptionHandler(value = {UserAllReadyExistException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleUserAllReadyExistException(UserAllReadyExistException e) {
        return new ErrorResponseDto(5, e.getMessage());
    }

    @ExceptionHandler(value = {ElementDoesNotExistException.class})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public ErrorResponseDto handleElementDoesNotExistException(ElementDoesNotExistException e) {
        return new ErrorResponseDto(6, e.getMessage());
    }

}

package com.disneymovie.disneyJava.controllers;

import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.MovieGenreModel;
import com.disneymovie.disneyJava.services.MovieGenreService;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import com.disneymovie.disneyJava.user.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class MovieGenreControllerTest {
    AutoCloseable openMocks;

    private MovieGenreController movieGenreController;

    @Mock
    private SessionManager sessionManager;
    @Mock
    MovieGenreService movieGenreService;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        //init session
        User user = new User(1, UserRole.admin, "admin@admin.com", "admin");
        String token = sessionManager.createSession(user);
        when(sessionManager.getCurrentUser(token)).thenReturn(user);

        movieGenreController = new MovieGenreController(movieGenreService);
    }

    @Test
    void findAllTestOk() throws SQLException {
        List<MovieGenreModel> array = new ArrayList<>();
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);
        array.add(model);
        when(movieGenreService.findAll()).thenReturn(array);

        ResponseEntity<?> response = movieGenreController.findAll();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(),array);
        verify(movieGenreService, times(1)).findAll();
    }

    @Test
    void findAllTestNoContent() throws SQLException {
        when(movieGenreService.findAll()).thenReturn(new ArrayList<>());

        ResponseEntity<?> response = movieGenreController.findAll();
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
    }

    @Test
    void findAllTestJpySystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException(""))))
                .when(movieGenreService).findAll();

        Assertions.assertThrows(SQLException.class, () -> movieGenreController.findAll());
    }

    @Test
    void findByIdOk() throws DataValidationException, SQLException {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);
        when(movieGenreService.findById(1)).thenReturn(Optional.of(model));

        ResponseEntity<?> response = movieGenreController.findById(1);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), model);
        verify(movieGenreService, times(1)).findById(1);

    }

    @Test
    void findByIdNoContent() throws DataValidationException, SQLException {
        when(movieGenreService.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = movieGenreController.findById(1);

        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        verify(movieGenreService, times(1)).findById(1);
    }

    @Test
    void findByIdJpaSystemException() {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);
        doThrow(new JpaSystemException(new RuntimeException(new SQLException(""))))
                .when(movieGenreService).findById(1);

        Assertions.assertThrows(SQLException.class, () -> movieGenreController.findById(1));
    }

    @Test
    void findByIdDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieGenreController.findById(0));
    }

    @Test
    void findByGenreOk() throws DataValidationException, SQLException {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);

        when(movieGenreService.findByGenre("comedia")).thenReturn(model);

        ResponseEntity<?> response = movieGenreController.findByGenre("comedia");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(response.getBody(), model);
        verify(movieGenreService, times(1)).findByGenre("comedia");
    }

    @Test
    void findByGenreNoContent() throws DataValidationException, SQLException {
        when(movieGenreService.findByGenre("comedia")).thenReturn(null);

        ResponseEntity<?> response = movieGenreController.findByGenre("comedia");
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        Assertions.assertNull(response.getBody());
        verify(movieGenreService, times(1)).findByGenre("comedia");
    }

    @Test
    void findByGenreJpaSystemException() {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);

        doThrow(new JpaSystemException(new RuntimeException(new SQLException(""))))
                .when(movieGenreService).findByGenre("comedia");

        Assertions.assertThrows(SQLException.class, () -> movieGenreController.findByGenre("comedia"));
    }

    @Test
    void findByGenreDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieGenreController.findByGenre(""));
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

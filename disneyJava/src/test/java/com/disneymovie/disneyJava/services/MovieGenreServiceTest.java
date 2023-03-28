package com.disneymovie.disneyJava.services;

import com.disneymovie.disneyJava.models.MovieGenreModel;
import com.disneymovie.disneyJava.projections.MovieGenreProjection;
import com.disneymovie.disneyJava.projections.MovieProjection;
import com.disneymovie.disneyJava.repositories.MovieGenreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class MovieGenreServiceTest {
    AutoCloseable openMocks;

    private MovieGenreService movieGenreService;

    @Mock
    MovieGenreRepository movieGenreRepository;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        movieGenreService = new MovieGenreService(movieGenreRepository);
    }

    @Test
    void findAllTestOk() {
        List<MovieGenreModel> list = new ArrayList<>();
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);
        list.add(model);

        when(movieGenreRepository.findAll()).thenReturn(list);
        Assertions.assertEquals(list,movieGenreService.findAll());
        verify(movieGenreRepository, times(1)).findAll();
    }

    @Test
    void findByGenreTestOk() {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);

        when(movieGenreRepository.findByGenre("comedia")).thenReturn(model);
        Assertions.assertEquals(model,movieGenreService.findByGenre("comedia"));
        verify(movieGenreRepository, times(1)).findByGenre("comedia");
    }

    @Test
    void findByIdTestOk() {
        MovieGenreModel model = new MovieGenreModel();
        model.setIdMovieGenre(1);

        when(movieGenreRepository.findById(1)).thenReturn(Optional.of(model));
        Assertions.assertEquals(model,movieGenreService.findById(1).get());
        verify(movieGenreRepository, times(1)).findById(1);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

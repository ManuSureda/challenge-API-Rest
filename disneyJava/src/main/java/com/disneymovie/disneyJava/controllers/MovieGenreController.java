package com.disneymovie.disneyJava.controllers;

import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.MovieGenreModel;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.MovieProjection;
import com.disneymovie.disneyJava.services.MovieGenreService;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
public class MovieGenreController {

    @Autowired
    private final MovieGenreService movieGenreService;

    public MovieGenreController(MovieGenreService movieGenreService) {
        this.movieGenreService = movieGenreService;
    }

    @GetMapping()
    public ResponseEntity<List<MovieGenreModel>> findAll() throws SQLException {
        try {
            List<MovieGenreModel> response = movieGenreService.findAll();
            if (response.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().body(response);
            }
        } catch (JpaSystemException e) {
            throw new SQLException(e.getCause().getCause().getMessage());
        }
    }

    @GetMapping("/{idMovieGenre}")
    public ResponseEntity<MovieGenreModel> findById(@PathVariable("idMovieGenre") final Integer idMovieGenre) throws DataValidationException, SQLException {
        if (idMovieGenre > 0) {
            try {
                Optional<MovieGenreModel> response = movieGenreService.findById(idMovieGenre);
                if (response.isPresent()) {
                    return ResponseEntity.ok().body(response.get());
                } else {
                    return ResponseEntity.noContent().build();
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Genre ID must be positive");
        }
    }

    @GetMapping(params = {"genre"})
    public ResponseEntity<MovieGenreModel> findByGenre(@RequestParam final String genre) throws DataValidationException, SQLException {
        if (!StringUtils.isBlank(genre)) {
            try {
                MovieGenreModel response = movieGenreService.findByGenre(genre);
                if (response == null) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok().body(response);
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Genre cant be null, void or blank");
        }
    }
}

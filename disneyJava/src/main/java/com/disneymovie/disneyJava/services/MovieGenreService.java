package com.disneymovie.disneyJava.services;

import com.disneymovie.disneyJava.models.MovieGenreModel;
import com.disneymovie.disneyJava.repositories.MovieGenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieGenreService {
    @Autowired
    private final MovieGenreRepository movieGenreRepository;

    public MovieGenreService(MovieGenreRepository movieGenreRepository) {
        this.movieGenreRepository = movieGenreRepository;
    }

    public List<MovieGenreModel> findAll() {
        return movieGenreRepository.findAll();
    }

    public MovieGenreModel findByGenre(String genre) {
        return movieGenreRepository.findByGenre(genre);
    }

    public Optional<MovieGenreModel> findById(Integer idMovieGenre) {
        return movieGenreRepository.findById(idMovieGenre);
    }
}

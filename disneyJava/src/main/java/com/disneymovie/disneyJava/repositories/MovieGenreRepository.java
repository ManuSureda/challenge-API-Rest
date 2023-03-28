package com.disneymovie.disneyJava.repositories;

import com.disneymovie.disneyJava.models.MovieGenreModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieGenreRepository extends JpaRepository<MovieGenreModel, Integer> {

    MovieGenreModel findByGenre(String genre);
}

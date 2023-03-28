package com.disneymovie.disneyJava.repositories;

import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.MovieGenreProjection;
import com.disneymovie.disneyJava.projections.MovieProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<MovieModel, Integer> {

    @Query(value = "select * from v_movies_by_character where id_character = ?", nativeQuery = true)
    List<MovieModel> getMoviesByCharacterId(Integer idMovie);

    @Query(value = "select * from v_genres_x_movie where id_movie = ?", nativeQuery = true)
    List<MovieGenreProjection> getMovieGenresByMovieId(Integer idMovie);

    @Query(value = "select * from v_movie_resume", nativeQuery = true)
    List<MovieProjection> getAllMovieResume();

    @Procedure(procedureName = "sp_create_movie")
    Integer createMovie(
            @Param("pImgUrl") String imgUrl,
            @Param("pTittle") String tittle,
            @Param("pReleaseDate") Date releaseDate,
            @Param("pScore") Integer score
    ) throws SQLException;

    @Transactional
    @Modifying
    @Query(value = "insert into genre_x_movie (id_movie, id_movie_genre) values (?, ?)", nativeQuery = true)
    void addGenreToMovie(Integer newID, Integer idMovieGenre) throws SQLException;


    @Transactional
    @Modifying
    @Query(value = "insert into character_x_movie(id_movie, id_character) values (?, ?)", nativeQuery = true)
    void addCharacterToMovie(Integer newID, Integer idCharacter) throws DataValidationException, SQLException;

    @Procedure(procedureName = "sp_update_movie")
    void updateMovies(
            @Param("pId_movie") Integer idMovie,
            @Param("pImgUrl") String imgUrl,
            @Param("pTittle") String tittle,
            @Param("pReleaseDate") Date releaseDate,
            @Param("pScore") Integer score);

    MovieModel findByTittle(String name);

    @Query(value = "select * from v_movie_by_genre_id where id_movie_genre = ?", nativeQuery = true)
    List<MovieModel> findByGenreId(Integer genreId);

    List<MovieModel> findAllByOrderByReleaseDateAsc();

    List<MovieModel> findAllByOrderByReleaseDateDesc();
}

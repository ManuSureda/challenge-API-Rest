package com.disneymovie.disneyJava.services;

import com.disneymovie.disneyJava.dtos.MovieModelDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.models.MovieGenreModel;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.MovieGenreProjection;
import com.disneymovie.disneyJava.projections.MovieProjection;
import com.disneymovie.disneyJava.repositories.CharacterRepository;
import com.disneymovie.disneyJava.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
    @Autowired
    private final MovieRepository movieRepository;
    @Autowired
    private final CharacterRepository characterRepository;

    public MovieService(MovieRepository movieRepository, CharacterRepository characterRepository) {
        this.movieRepository = movieRepository;
        this.characterRepository = characterRepository;
    }

    public Optional<MovieModel> findById(Integer id) {
        return movieRepository.findById(id);
    }

    public List<MovieModelDto> getMoviesByCharacterId(Integer idCharacter) {
        List<MovieModel> resultSet = movieRepository.getMoviesByCharacterId(idCharacter);
        List<MovieGenreProjection> genresResulSet = new ArrayList<>();
        ArrayList<MovieModelDto> response = new ArrayList<>();
        MovieModelDto dto = new MovieModelDto();
        if (!resultSet.isEmpty()) {
            for (MovieModel movie: resultSet) {
                dto = new MovieModelDto();
                dto.setIdMovie(movie.getIdMovie());
                dto.setImgUrl(movie.getImgUrl());
                dto.setTittle(movie.getTittle());
                dto.setReleaseDate(movie.getReleaseDate());
                dto.setScore(movie.getScore());

                genresResulSet = movieRepository.getMovieGenresByMovieId(dto.getIdMovie());
                if (!genresResulSet.isEmpty()) {
                    for (MovieGenreProjection genre: genresResulSet) {
                        dto.getGenres().add(new MovieGenreModel(
                                genre.getId_movie_genre(),
                                genre.getGenre(),
                                genre.getImg_url()));
                    }
                }

                response.add(dto);
            }
        }
        return response;
    }

    public List<MovieProjection> getAllMovieResume() {
        return movieRepository.getAllMovieResume();
    }

    public List<MovieModelDto> getAllMoviesAndCharacters() {
        List<MovieModelDto> response = new ArrayList<>();
        MovieModelDto dto = new MovieModelDto();

        List<MovieModel> resulSetMovie = movieRepository.findAll();
        List<MovieGenreProjection> movieGenreProjectionList = new ArrayList<>();
        List<CharacterModel> characters = new ArrayList<>();
        if (!resulSetMovie.isEmpty()) {
            for (MovieModel movie: resulSetMovie) {
                dto = new MovieModelDto();
                dto.setIdMovie(movie.getIdMovie());
                dto.setImgUrl(movie.getImgUrl());
                dto.setTittle(movie.getTittle());
                dto.setReleaseDate(movie.getReleaseDate());
                dto.setScore(movie.getScore());

                movieGenreProjectionList = movieRepository.getMovieGenresByMovieId(movie.getIdMovie());
                if (!movieGenreProjectionList.isEmpty()) {
                    for (MovieGenreProjection genre: movieGenreProjectionList) {
                        dto.getGenres().add(new MovieGenreModel(
                                genre.getId_movie_genre(),
                                genre.getGenre(),
                                movie.getImgUrl()
                        ));
                    }
                }

                characters = characterRepository.findByMovieId(dto.getIdMovie());
                if (!characters.isEmpty()) {
                    for (CharacterModel newCharacter: characters) {
                        dto.getCharacters().add(newCharacter);
                    }
                }
                response.add(dto);
            }
        }
        return response;
    }

    public Integer createMovie(MovieModelDto newMovie) throws SQLException, JpaSystemException, DataIntegrityViolationException, DataValidationException {

        Integer newID = movieRepository.createMovie(
                newMovie.getImgUrl(),
                newMovie.getTittle(),
                newMovie.getReleaseDate(),
                newMovie.getScore()
        );

        if (newID > 0) {
            if (!newMovie.getGenresIdList().isEmpty()) {
                for (Integer genreId: newMovie.getGenresIdList()) {
                    movieRepository.addGenreToMovie(newID, genreId
                    );// aca es donde se puede generar un DataIntegrityViolationException que serea cacheado en la controller
                }
            }

            if (!newMovie.getCharactersIdList().isEmpty()) {
                for (Integer characterId: newMovie.getCharactersIdList()) {
                    movieRepository.addCharacterToMovie(
                            newID,
                            characterId
                    );
                }
            }
        }
        return newID;
    }

    public void addCharactersToMovie(final Integer idMovie, final Integer idCharacter) throws DataValidationException, SQLException {
        movieRepository.addCharacterToMovie(idMovie, idCharacter);
    }

    public MovieModelDto getMovieDtoById(Integer idMovie) {
        if (findById(idMovie).isPresent()) {
            MovieModel model = findById(idMovie).get();

            MovieModelDto response = new MovieModelDto();
            response.setIdMovie(model.getIdMovie());
            response.setScore(model.getScore());
            response.setImgUrl(model.getImgUrl());
            response.setTittle(model.getTittle());
            response.setReleaseDate(model.getReleaseDate());

            List<MovieGenreModel> genres = getGenresModelByMovieId(response.getIdMovie());
            response.setGenres(genres);

            List<CharacterModel> characters = getCharactersModelByMovieId(response.getIdMovie());
            response.setCharacters(characters);

            return response;
        } else {
            return null;
        }
    }

    public List<CharacterModel> getCharactersModelByMovieId(Integer newMovieId) {
        return characterRepository.findByMovieId(newMovieId);
    }

    public List<MovieGenreModel> getGenresModelByMovieId(Integer newMovieId) {
        List<MovieGenreProjection> movieGenreProjectionList = movieRepository.getMovieGenresByMovieId(newMovieId);
        List<MovieGenreModel> response = new ArrayList<>();
        if (!movieGenreProjectionList.isEmpty()) {
            for (MovieGenreProjection genre: movieGenreProjectionList) {
                response.add(new MovieGenreModel(
                        genre.getId_movie_genre(),
                        genre.getGenre(),
                        genre.getImg_url()
                ));
            }
        }

        return response;
    }

    public void updateMovie(MovieModelDto movieModified) {
        movieRepository.updateMovies(
                movieModified.getIdMovie(),
                movieModified.getImgUrl(),
                movieModified.getTittle(),
                movieModified.getReleaseDate(),
                movieModified.getScore()
        );
    }

    public void addGenreToMovie(final Integer idMovie, final Integer idGenre) throws SQLException {
        movieRepository.addGenreToMovie(idMovie, idGenre);
    }

    public void deleteById(Integer id) {
        movieRepository.deleteById(id);
    }

    public MovieModel findByTittle(String name) {
        return movieRepository.findByTittle(name);
    }

    public List<MovieModel> findByGenreId(Integer genreId) {
        return movieRepository.findByGenreId(genreId);
    }

    public List<MovieModel> findAllByOrderByReleaseDateAsc() {
        return movieRepository.findAllByOrderByReleaseDateAsc();
    }

    public List<MovieModel> findAllByOrderByReleaseDateDesc() {
        return movieRepository.findAllByOrderByReleaseDateDesc();
    }
}

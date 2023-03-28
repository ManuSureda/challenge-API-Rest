package com.disneymovie.disneyJava.controllers;

import com.disneymovie.disneyJava.dtos.MovieModelDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.MovieProjection;
import com.disneymovie.disneyJava.services.MovieService;
import com.disneymovie.disneyJava.user.model.User;
import com.disneymovie.disneyJava.user.model.UserRole;
import com.disneymovie.disneyJava.user.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

public class MovieControllerTest {
    AutoCloseable openMocks;

    private MovieController movieController;
    private MovieProjection movieProjection;

    @Mock
    private SessionManager sessionManager;
    @Mock
    MovieService movieService;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        //init session
        User user = new User(1, UserRole.admin, "admin@admin.com", "admin");
        String token = sessionManager.createSession(user);
        when(sessionManager.getCurrentUser(token)).thenReturn(user);

        movieController = new MovieController(movieService);
        ProjectionFactory factoryCall = new SpelAwareProxyProjectionFactory();
        movieProjection = factoryCall.createProjection(MovieProjection.class);
    }

    @Test
    void getMoviesByCharacterIdTestOk() throws DataValidationException {
        List<MovieModelDto> mockedList = new ArrayList<>();
        MovieModelDto mockedDto = new MovieModelDto(1,"","tittle",new Date(),1,null,null,null,null);
        mockedList.add(mockedDto);

        when(movieService.getMoviesByCharacterId(1)).thenReturn(mockedList);

        ResponseEntity<List<MovieModelDto>> response = movieController.getMoviesByCharacterId(1);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(mockedList, response.getBody());
        verify(movieService, times(1)).getMoviesByCharacterId(1);
    }

    @Test
    void getMoviesByCharacterIdTestNoContent() throws DataValidationException {
        List<MovieModelDto> mockedList = new ArrayList<>();

        when(movieService.getMoviesByCharacterId(1)).thenReturn(mockedList);

        ResponseEntity<List<MovieModelDto>> response = movieController.getMoviesByCharacterId(1);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).getMoviesByCharacterId(1);
    }

    @Test
    void getMoviesByCharacterIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class,
                () -> movieController.getMoviesByCharacterId(0));
    }

    @Test
    void getAllMovieResumeTestOk() throws SQLException {
        List<MovieProjection> movieProjectionsList = new ArrayList<>();
        movieProjection.setImg_url("");
        movieProjection.setTittle("tittle");
        movieProjection.setRelease_date(new Date());

        movieProjectionsList.add(movieProjection);

        when(movieService.getAllMovieResume()).thenReturn(movieProjectionsList);

        ResponseEntity<List<MovieProjection>> response = movieController.getAllMovieResume();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(response.getBody(), movieProjectionsList);
        verify(movieService, times(1)).getAllMovieResume();
    }

    @Test
    void getAllMovieResumeTestNoContent() throws SQLException {
        List<MovieProjection> movieProjectionsList = new ArrayList<>();

        when(movieService.getAllMovieResume()).thenReturn(movieProjectionsList);

        ResponseEntity<List<MovieProjection>> response = movieController.getAllMovieResume();

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).getAllMovieResume();
    }

    @Test
    void getAllMovieResumeTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).getAllMovieResume();

        Assertions.assertThrows(SQLException.class, () -> movieController.getAllMovieResume());
    }

    @Test
    void getAllMoviesAndCharactersTestOk() throws SQLException {
        List<MovieModelDto> list = new ArrayList<>();
        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        list.add(dto);

        when(movieService.getAllMoviesAndCharacters()).thenReturn(list);

        ResponseEntity<List<MovieModelDto>> response = movieController.getAllMoviesAndCharacters();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(list, response.getBody());
        verify(movieService, times(1)).getAllMoviesAndCharacters();
    }

    @Test
    void getAllMoviesAndCharactersTestNoContent() throws SQLException {
        List<MovieModelDto> list = new ArrayList<>();

        when(movieService.getAllMoviesAndCharacters()).thenReturn(list);

        ResponseEntity<List<MovieModelDto>> response = movieController.getAllMoviesAndCharacters();

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).getAllMoviesAndCharacters();
    }

    @Test
    void getAllMoviesAndCharactersTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).getAllMoviesAndCharacters();

        Assertions.assertThrows(SQLException.class, () -> movieController.getAllMoviesAndCharacters());
    }

    @Test
    void createMovieTestOk() throws DataValidationException, SQLException, URISyntaxException {
        MovieModelDto newMovie = new MovieModelDto();
        newMovie.setIdMovie(1);
        newMovie.setImgUrl("a");
        newMovie.setTittle("da");
        newMovie.setReleaseDate(new Date());
        newMovie.setScore(2);

        when(movieService.createMovie(newMovie)).thenReturn(1);


        ResponseEntity<MovieModelDto> mockedResponse = ResponseEntity.created(new URI("http://localhost:8080/movies/"+1)).body(newMovie);
        ResponseEntity<MovieModelDto> response = movieController.createMovie(newMovie);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals(mockedResponse, response);
        verify(movieService, times(1)).createMovie(newMovie);
    }

    @Test
    void createMovieTestJpaSystemException() throws SQLException, DataValidationException {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);
        movieModelDto.setImgUrl("a");
        movieModelDto.setTittle("da");
        movieModelDto.setReleaseDate(new Date());
        movieModelDto.setScore(2);
        doThrow(new DataIntegrityViolationException(""))
                .when(movieService).createMovie(movieModelDto);

        Assertions.assertThrows(DataValidationException.class, () -> movieController.createMovie(movieModelDto));
    }

    @Test
    void createMovieTestDataValidationException() {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);

        Assertions.assertThrows(DataValidationException.class, () -> movieController.createMovie(movieModelDto));
    }

    @Test
    void getMovieByIdOk() throws DataValidationException {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);
        movieModelDto.setImgUrl("a");
        movieModelDto.setTittle("da");
        movieModelDto.setReleaseDate(new Date());
        movieModelDto.setScore(2);

        when(movieService.getMovieDtoById(1)).thenReturn(movieModelDto);

        ResponseEntity<MovieModelDto> response = movieController.getMovieById(1);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(movieModelDto, response.getBody());
        verify(movieService, times(1)).getMovieDtoById(1);
    }

    @Test
    void getMovieByIdNoContent() throws DataValidationException {
        when(movieService.getMovieDtoById(1)).thenReturn(null);

        ResponseEntity<MovieModelDto> response = movieController.getMovieById(1);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).getMovieDtoById(1);
    }

    @Test
    void getMovieByIdDataValidationException() {
        Assertions.assertThrows(DataValidationException.class,
                () -> movieController.getMovieById(-1));
    }

    @Test
    void updateMovieTestOk() throws SQLException, DataValidationException {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);
        movieModelDto.setImgUrl("a");
        movieModelDto.setTittle("da");
        movieModelDto.setReleaseDate(new Date());
        movieModelDto.setScore(2);

        ArrayList<Integer> idMovieGenresList = new ArrayList<>();
        idMovieGenresList.add(1);
        movieModelDto.setGenresIdList(idMovieGenresList);
        movieModelDto.setCharactersIdList(idMovieGenresList);

        doNothing().when(movieService).updateMovie(movieModelDto);
        doNothing().when(movieService).addGenreToMovie(1,1);
        doNothing().when(movieService).addCharactersToMovie(1,1);

        ResponseEntity<?> response = movieController.updateMovie(movieModelDto);

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(movieService, times(1)).updateMovie(movieModelDto);
    }

    @Test
    void updateMovieTestJpaSystemException() {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);
        movieModelDto.setImgUrl("a");
        movieModelDto.setTittle("da");
        movieModelDto.setReleaseDate(new Date());
        movieModelDto.setScore(2);

        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).updateMovie(movieModelDto);

        Assertions.assertThrows(SQLException.class, () -> movieController.updateMovie(movieModelDto));
    }

    @Test
    void updateMovieTestDataValidationException() {
        MovieModelDto movieModelDto = new MovieModelDto();
        movieModelDto.setIdMovie(1);

        Assertions.assertThrows(DataValidationException.class, () -> movieController.updateMovie(movieModelDto));
    }

    @Test
    void deleteMovieByIdTestAccepted() throws DataValidationException, SQLException {
        doNothing().when(movieService).deleteById(1);
        ResponseEntity<?> response = movieController.deleteMovieById(1);
        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        verify(movieService, times(1)).deleteById(1);
    }

    @Test
    void deleteMovieByIdTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).deleteById(1);
        Assertions.assertThrows(SQLException.class, () -> movieController.deleteMovieById(1));
    }

    @Test
    void deleteMovieByIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieController.deleteMovieById(0));
    }

    @Test
    void findByTittleTestOk() throws DataValidationException, SQLException {
        MovieModel movieModel = new MovieModel(1,1,"","t",new Date());

        when(movieService.findByTittle("t")).thenReturn(movieModel);

        ResponseEntity<MovieModel> response = movieController.findByTittle("t");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(movieModel, response.getBody());
        verify(movieService, times(1)).findByTittle("t");
    }

    @Test
    void findByTittleTestNoContent() throws DataValidationException, SQLException {
        ResponseEntity<MovieModel> response = movieController.findByTittle("t");

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).findByTittle("t");
    }

    @Test
    void findByTittleTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).findByTittle("t");

        Assertions.assertThrows(SQLException.class, () -> movieController.findByTittle("t"));
    }

    @Test
    void findByNameTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieController.findByTittle(""));
    }

    @Test
    void findByGenreIdTestOk() throws DataValidationException {
        List<MovieModel> list = new ArrayList<>();
        MovieModel movieModel = new MovieModel(1,1,"","t",new Date());
        list.add(movieModel);

        when(movieService.findByGenreId(1)).thenReturn(list);

        ResponseEntity<List<MovieModel>> response = movieController.findByGenreId(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(list, response.getBody());
        verify(movieService, times(1)).findByGenreId(1);
    }

    @Test
    void findByGenreIdTestNoContent() throws DataValidationException {
        List<MovieModel> list = new ArrayList<>();

        when(movieService.findByGenreId(1)).thenReturn(list);

        ResponseEntity<List<MovieModel>> response = movieController.findByGenreId(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).findByGenreId(1);
    }

    @Test
    void findByGenreIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieController.findByGenreId(0));
    }

    @Test
    void findAllByOrderTestOk() throws DataValidationException, SQLException {
        List<MovieModel> list = new ArrayList<>();
        MovieModel movieModel = new MovieModel(1,1,"","t",new Date());
        list.add(movieModel);

        when(movieService.findAllByOrderByReleaseDateAsc()).thenReturn(list);

        ResponseEntity<List<MovieModel>> response = movieController.findAllByOrder("ASC");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(list, response.getBody());
        verify(movieService, times(1)).findAllByOrderByReleaseDateAsc();

        when(movieService.findAllByOrderByReleaseDateDesc()).thenReturn(list);
        response = movieController.findAllByOrder("DESC");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(list, response.getBody());
        verify(movieService, times(1)).findAllByOrderByReleaseDateAsc();
    }

    @Test
    void findAllByOrderTestNoContent() throws DataValidationException, SQLException {
        List<MovieModel> list = new ArrayList<>();

        when(movieService.findAllByOrderByReleaseDateAsc()).thenReturn(list);

        ResponseEntity<List<MovieModel>> response = movieController.findAllByOrder("ASC");

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).findAllByOrderByReleaseDateAsc();

        when(movieService.findAllByOrderByReleaseDateDesc()).thenReturn(list);
        response = movieController.findAllByOrder("DESC");

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(movieService, times(1)).findAllByOrderByReleaseDateAsc();
    }

    @Test
    void findAllByOrderTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).findAllByOrderByReleaseDateAsc();
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(movieService).findAllByOrderByReleaseDateDesc();

        Assertions.assertThrows(SQLException.class, () -> movieController.findAllByOrder("ASC"));
        Assertions.assertThrows(SQLException.class, () -> movieController.findAllByOrder("DESC"));
    }

    @Test
    void findAllByOrderTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> movieController.findAllByOrder("pepe"));
    }

    @Test
    void addCharacterToMovieTestOk() throws DataValidationException, SQLException {
        final Integer movieId = 1;
        final Integer characterId = 1;
        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setCharacters(new ArrayList<>());
        when(movieService.getMovieDtoById(movieId)).thenReturn(dto);
        doNothing().when(movieService).addCharactersToMovie(movieId,characterId);

        ResponseEntity<?> response = movieController.addCharacterToMovie(movieId,characterId);
        Assertions.assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);
        verify(movieService, times(1)).addCharactersToMovie(movieId,characterId);

    }

    @Test
    void addCharacterToMovieTestSQLException() {
        final Integer movieId = 1;
        final Integer characterId = 1;
        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        CharacterModel model = new CharacterModel();
        model.setIdCharacter(1);
        ArrayList<CharacterModel> characters = new ArrayList<>();
        characters.add(model);
        dto.setCharacters(characters);
        when(movieService.getMovieDtoById(movieId)).thenReturn(dto);

        Assertions.assertThrows(SQLException.class, () -> movieController.addCharacterToMovie(movieId,characterId));
    }

    @Test
    void addCharacterToMovieTestDataValidationException() throws DataValidationException, SQLException {
        final Integer movieId = 1;
        final Integer characterId = 1;
        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setCharacters(new ArrayList<>());
        when(movieService.getMovieDtoById(movieId)).thenReturn(dto);
        doNothing().when(movieService).addCharactersToMovie(movieId,characterId);

        doThrow(new SQLException(""))
                .when(movieService).addCharactersToMovie(1,1);

        Assertions.assertThrows(DataValidationException.class, () -> movieController.addCharacterToMovie(movieId,characterId));
        Assertions.assertThrows(DataValidationException.class, () -> movieController.addCharacterToMovie(0,0));

    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

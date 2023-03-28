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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class MovieServiceTest {
    AutoCloseable openMocks;
    private MovieService movieService;
    private MovieProjection movieProjection;
    private MovieGenreProjection movieGenreProjection;

    @Mock
    MovieRepository movieRepository;
    @Mock
    CharacterRepository characterRepository;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        movieService = new MovieService(movieRepository,characterRepository);

        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        this.movieProjection = factory.createProjection(MovieProjection.class);
        this.movieGenreProjection = factory.createProjection(MovieGenreProjection.class);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    void findByIdTest() {
        MovieModel mockedModel = new MovieModel(1,1,"i","t",new Date());

        when(movieRepository.findById(1)).thenReturn(Optional.of(mockedModel));
        Optional<MovieModel> response = movieService.findById(1);

        Assertions.assertEquals(response.get(), mockedModel);
        Assertions.assertNotNull(response.get());
        verify(movieRepository, times(1)).findById(1);
    }

    @Test
    void getMoviesByCharacterIdTest() {
        Date date = new Date();
        MovieModel mockedModel = new MovieModel(1,1,"i","t",date);
        List<MovieModel> mockedList = new ArrayList<>();
        mockedList.add(mockedModel);
        when(movieRepository.getMoviesByCharacterId(1)).thenReturn(mockedList);

        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setScore(1);
        dto.setImgUrl("i");
        dto.setTittle("t");
        dto.setReleaseDate(date);

        List<MovieGenreProjection> genresResulSet = new ArrayList<>();
        movieGenreProjection.setId_movie_genre(1);
        movieGenreProjection.setGenre("comedia");
        movieGenreProjection.setImg_url("a");
        genresResulSet.add(movieGenreProjection);

        List<MovieGenreModel> genresResulSetModel = new ArrayList<>();
        MovieGenreModel mg = new MovieGenreModel();
        mg.setIdMovieGenre(1);
        mg.setGenre("comedia");
        mg.setImgUrl("a");
        genresResulSetModel.add(mg);
        dto.setGenres(genresResulSetModel);

        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(genresResulSet);
        List<MovieModelDto> responseDTO = new ArrayList<>();
        responseDTO.add(dto);

        List<MovieModelDto> response = movieService.getMoviesByCharacterId(1);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response,responseDTO);
        verify(movieRepository, times(1)).getMoviesByCharacterId(1);
    }

    @Test
    void getMoviesByCharacterIdGenresEmptyTest() {
        Date date = new Date();
        MovieModel mockedModel = new MovieModel(1,1,"i","t",date);
        List<MovieModel> mockedList = new ArrayList<>();
        mockedList.add(mockedModel);
        when(movieRepository.getMoviesByCharacterId(1)).thenReturn(mockedList);

        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setScore(1);
        dto.setImgUrl("i");
        dto.setTittle("t");
        dto.setReleaseDate(date);

        List<MovieGenreProjection> genresResulSet = new ArrayList<>();
        List<MovieGenreModel> genresResulSetModel = new ArrayList<>();
        dto.setGenres(genresResulSetModel);

        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(genresResulSet);
        List<MovieModelDto> responseDTO = new ArrayList<>();
        responseDTO.add(dto);

        List<MovieModelDto> response = movieService.getMoviesByCharacterId(1);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response,responseDTO);
        verify(movieRepository, times(1)).getMoviesByCharacterId(1);
    }

    @Test
    void getMoviesByCharacterIdMoviesEmptyTest() {
        List<MovieModel> mockedList = new ArrayList<>();
        when(movieRepository.getMoviesByCharacterId(1)).thenReturn(mockedList);

//        MovieModelDto dto = new MovieModelDto();
        List<MovieModelDto> response = new ArrayList<>();

        Assertions.assertEquals(movieService.getMoviesByCharacterId(1),response);
    }

    @Test
    void getAllMovieResumeTest() {
        List<MovieProjection> mockedList = new ArrayList<>();
        movieProjection.setIdMovie(1);
        Date date = new Date();
        movieProjection.setRelease_date(date);
        movieProjection.setTittle("t");
        movieProjection.setImg_url("a");
        movieProjection.setScore(1);
        mockedList.add(movieProjection);

        when(movieRepository.getAllMovieResume()).thenReturn(mockedList);
        List<MovieProjection> res = movieService.getAllMovieResume();

        Assertions.assertEquals(res, mockedList);
        verify(movieRepository, times(1)).getAllMovieResume();
    }

    @Test
    void getAllMoviesAndCharacters() {
        List<MovieModelDto> mockMovieModelDtoList = new ArrayList<>();
        Date date = new Date();
        List<MovieGenreModel> mockedGenresList = new ArrayList<>();
        MovieGenreModel mockedMovieGenreModel = new MovieGenreModel(1,"genre","img_url");
        mockedGenresList.add(mockedMovieGenreModel);
        List<CharacterModel> mockedCharacterList = new ArrayList<>();
        CharacterModel mockedCharacterModel = new CharacterModel(1,1,1,"a","n","s");
        mockedCharacterList.add(mockedCharacterModel);
        MovieModelDto mockMovieModelDto = new MovieModelDto(
                1,"img_url","tittle",date,1,mockedGenresList,new ArrayList<>(),mockedCharacterList,new ArrayList<>()
        );
        mockMovieModelDtoList.add(mockMovieModelDto);

        List<MovieModel> mockedMovieModelList = new ArrayList<>();
        MovieModel mockedMovieModel = new MovieModel(1, 1,"img_url","tittle",date);
        mockedMovieModelList.add(mockedMovieModel);

        when(movieRepository.findAll()).thenReturn(mockedMovieModelList);

        List<MovieGenreProjection> mockedMovieGenreProjectionList = new ArrayList<>();
        movieGenreProjection.setId_movie(1);
        movieGenreProjection.setGenre("genre");
        movieGenreProjection.setImg_url("img_url");
        movieGenreProjection.setId_movie_genre(1);
        mockedMovieGenreProjectionList.add(movieGenreProjection);
        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(mockedMovieGenreProjectionList);

        when(characterRepository.findByMovieId(1)).thenReturn(mockedCharacterList);

        Assertions.assertNotNull(movieService.getAllMoviesAndCharacters());
        Assertions.assertEquals(mockMovieModelDtoList, movieService.getAllMoviesAndCharacters());
        verify(movieRepository, times(2)).findAll();
        verify(movieRepository, times(2)).getMovieGenresByMovieId(1);
        verify(characterRepository, times(2)).findByMovieId(1);
    }

    @Test
    void getAllMoviesAndCharactersEmptyMoviesTest() {
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());
        List<MovieModelDto> response = movieService.getAllMoviesAndCharacters();
        Assertions.assertEquals(response, new ArrayList<>());
    }

    @Test
    void getAllMoviesAndCharactersGenresEmptyTest() {
        List<MovieModelDto> mockMovieModelDtoList = new ArrayList<>();
        Date date = new Date();
        List<CharacterModel> mockedCharacterList = new ArrayList<>();
        CharacterModel mockedCharacterModel = new CharacterModel(1,1,1,"a","n","s");
        mockedCharacterList.add(mockedCharacterModel);
        MovieModelDto mockMovieModelDto = new MovieModelDto(
                1,"img_url","tittle",date,1,new ArrayList<>(),new ArrayList<>(),mockedCharacterList,new ArrayList<>()
        );
        mockMovieModelDtoList.add(mockMovieModelDto);

        List<MovieModel> mockedMovieModelList = new ArrayList<>();
        MovieModel mockedMovieModel = new MovieModel(1, 1,"img_url","tittle",date);
        mockedMovieModelList.add(mockedMovieModel);

        when(movieRepository.findAll()).thenReturn(mockedMovieModelList);

        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(new ArrayList<>());

        when(characterRepository.findByMovieId(1)).thenReturn(mockedCharacterList);

        Assertions.assertNotNull(movieService.getAllMoviesAndCharacters());
        Assertions.assertEquals(mockMovieModelDtoList, movieService.getAllMoviesAndCharacters());
        verify(movieRepository, times(2)).findAll();
        verify(movieRepository, times(2)).getMovieGenresByMovieId(1);
        verify(characterRepository, times(2)).findByMovieId(1);
    }

    @Test
    void getAllMoviesAndCharactersEmptyCharactersTest() {
        List<MovieModelDto> mockMovieModelDtoList = new ArrayList<>();
        Date date = new Date();
        List<MovieGenreModel> mockedGenresList = new ArrayList<>();
        MovieGenreModel mockedMovieGenreModel = new MovieGenreModel(1,"genre","img_url");
        mockedGenresList.add(mockedMovieGenreModel);
        MovieModelDto mockMovieModelDto = new MovieModelDto(
                1,"img_url","tittle",date,1,mockedGenresList,new ArrayList<>(),new ArrayList<>(),new ArrayList<>()
        );
        mockMovieModelDtoList.add(mockMovieModelDto);

        List<MovieModel> mockedMovieModelList = new ArrayList<>();
        MovieModel mockedMovieModel = new MovieModel(1, 1,"img_url","tittle",date);
        mockedMovieModelList.add(mockedMovieModel);

        when(movieRepository.findAll()).thenReturn(mockedMovieModelList);

        List<MovieGenreProjection> mockedMovieGenreProjectionList = new ArrayList<>();
        movieGenreProjection.setId_movie(1);
        movieGenreProjection.setGenre("genre");
        movieGenreProjection.setImg_url("img_url");
        movieGenreProjection.setId_movie_genre(1);
        mockedMovieGenreProjectionList.add(movieGenreProjection);
        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(mockedMovieGenreProjectionList);

        when(characterRepository.findByMovieId(1)).thenReturn(new ArrayList<>());

        Assertions.assertNotNull(movieService.getAllMoviesAndCharacters());
        Assertions.assertEquals(mockMovieModelDtoList, movieService.getAllMoviesAndCharacters());
        verify(movieRepository, times(2)).findAll();
        verify(movieRepository, times(2)).getMovieGenresByMovieId(1);
        verify(characterRepository, times(2)).findByMovieId(1);
    }

    @Test
    void createMovieTest() throws SQLException, DataValidationException {
        Date date = new Date();
        when(movieRepository.createMovie("i","t",date,1)).thenReturn(1);
        MovieModelDto dto = new MovieModelDto();
        dto.setImgUrl("i");
        dto.setTittle("t");
        dto.setReleaseDate(date);
        dto.setScore(1);
        ArrayList<Integer> array = new ArrayList<>();
        array.add(1);
        dto.setGenresIdList(array);
        dto.setCharactersIdList(array);

        doNothing().when(movieRepository).addGenreToMovie(1,1);
        doNothing().when(movieRepository).addCharacterToMovie(1,1);
        Assertions.assertEquals(1,movieService.createMovie(dto));
    }

    @Test
    void createMovieNullIdTest() throws SQLException, DataValidationException {
        Date date = new Date();
        when(movieRepository.createMovie("i","t",date,1)).thenReturn(null);
        Assertions.assertEquals(0,movieService.createMovie(new MovieModelDto()));
    }

    @Test
    void createMovieGenresEmptyTest() throws SQLException, DataValidationException {
        Date date = new Date();
        when(movieRepository.createMovie("i","t",date,1)).thenReturn(1);
        MovieModelDto dto = new MovieModelDto();
        dto.setImgUrl("i");
        dto.setTittle("t");
        dto.setReleaseDate(date);
        dto.setScore(1);

        dto.setGenresIdList(new ArrayList<>());
        dto.setCharactersIdList(new ArrayList<>());

        doNothing().when(movieRepository).addGenreToMovie(1,1);
        doNothing().when(movieRepository).addCharacterToMovie(1,1);
        Assertions.assertEquals(1,movieService.createMovie(dto));
    }

    @Test
    void getMovieDtoByIdTestOk(){
        Date date = new Date();
        MovieModel mockedModel = new MovieModel(1,1,"i","t",date);

        when(movieRepository.findById(1)).thenReturn(Optional.of(mockedModel));

        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setScore(1);
        dto.setImgUrl("i");
        dto.setTittle("t");
        dto.setReleaseDate(date);

        List<MovieGenreProjection> mockedMovieGenreProjectionList = new ArrayList<>();
        movieGenreProjection.setId_movie(1);
        movieGenreProjection.setId_movie_genre(1);
        movieGenreProjection.setGenre("g");
        movieGenreProjection.setImg_url("m");
        mockedMovieGenreProjectionList.add(movieGenreProjection);
        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(mockedMovieGenreProjectionList);

        List<MovieGenreModel> mockedMovieGenreModel = movieService.getGenresModelByMovieId(1);
        dto.setGenres(mockedMovieGenreModel);

        List<CharacterModel> characterModelList = new ArrayList<>();
        CharacterModel cm = new CharacterModel(1,1,1,"img_url","name","story");
        characterModelList.add(cm);
        dto.setCharacters(characterModelList);
        when(characterRepository.findByMovieId(1)).thenReturn(characterModelList);

        Assertions.assertEquals(dto, movieService.getMovieDtoById(1));
    }

    @Test
    void getGenresModelByMovieIdEmptyTest() {
        when(movieRepository.getMovieGenresByMovieId(1)).thenReturn(new ArrayList<>());
        Assertions.assertEquals(new ArrayList<>(), movieService.getGenresModelByMovieId(1));
    }

    @Test
    void getMovieDtoByIdTestNull(){
        when(movieRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertNull(movieService.getMovieDtoById(1));
    }

    @Test
    void updateMovieTest() {
        Date date = new Date();
        doNothing().when(movieRepository).updateMovies(1,"img","tittle",date,1);

        MovieModelDto dto = new MovieModelDto();
        dto.setIdMovie(1);
        dto.setImgUrl("img");
        dto.setTittle("tittle");
        dto.setReleaseDate(date);
        dto.setScore(1);
        movieService.updateMovie(dto);

        verify(movieRepository, times(1)).updateMovies(1,"img","tittle",date,1);
    }

    @Test
    void deleteByIdTest() {
        doNothing().when(movieRepository).deleteById(1);
        movieService.deleteById(1);
        verify(movieRepository, times(1)).deleteById(1);
    }

    @Test
    void findByTittleTest() {
        Date date = new Date();
        MovieModel movieModel = new MovieModel(1,1,"i","t",date);
        when(movieRepository.findByTittle("t")).thenReturn(movieModel);
        Assertions.assertEquals(movieModel, movieService.findByTittle("t"));
        verify(movieRepository, times(1)).findByTittle("t");
    }

    @Test
    void findByGenreIdTest() {
        Date date = new Date();
        MovieModel movieModel = new MovieModel(1,1,"i","t",date);
        List<MovieModel> movieModelList = new ArrayList<>();
        movieModelList.add(movieModel);
        when(movieRepository.findByGenreId(1)).thenReturn(movieModelList);
        Assertions.assertEquals(movieModelList, movieService.findByGenreId(1));
        verify(movieRepository, times(1)).findByGenreId(1);
    }

    @Test
    void findAllByOrderByReleaseDateAscTest() {
        Date date = new Date();
        MovieModel movieModel = new MovieModel(1,1,"i","t",date);
        List<MovieModel> movieModelList = new ArrayList<>();
        movieModelList.add(movieModel);
        when(movieRepository.findAllByOrderByReleaseDateAsc()).thenReturn(movieModelList);
        Assertions.assertEquals(movieModelList, movieService.findAllByOrderByReleaseDateAsc());
        verify(movieRepository, times(1)).findAllByOrderByReleaseDateAsc();
    }

    @Test
    void findAllByOrderByReleaseDateDescTest() {
        Date date = new Date();
        MovieModel movieModel = new MovieModel(1,1,"i","t",date);
        List<MovieModel> movieModelList = new ArrayList<>();
        movieModelList.add(movieModel);
        when(movieRepository.findAllByOrderByReleaseDateDesc()).thenReturn(movieModelList);
        Assertions.assertEquals(movieModelList, movieService.findAllByOrderByReleaseDateDesc());
        verify(movieRepository, times(1)).findAllByOrderByReleaseDateDesc();
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

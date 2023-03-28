package com.disneymovie.disneyJava.controllers;

import com.disneymovie.disneyJava.dtos.CharacterModelDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.CharacterProjection;
import com.disneymovie.disneyJava.services.CharacterService;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Mockito.*;

public class CharacterControllerTest {
    AutoCloseable openMocks;

    private CharacterController characterController;
    private CharacterProjection characterProjection;

    @Mock
    private SessionManager sessionManager;
    @Mock
    private CharacterService characterService;
    @Mock
    private MovieService movieService;

    @BeforeEach
    void setUp() {
//        initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        //init session
        User user = new User(1, UserRole.admin, "admin@admin.com", "admin");
        String token = sessionManager.createSession(user);
        when(sessionManager.getCurrentUser(token)).thenReturn(user);

        //init controller
        characterController = new CharacterController(characterService,movieService);

        //init projection
        ProjectionFactory factoryCall = new SpelAwareProxyProjectionFactory();
        characterProjection = factoryCall.createProjection(CharacterProjection.class);
    }

    @Test
    void resumeAllCharactersOkTest() throws SQLException {
        characterProjection.setName("Manny");
        characterProjection.setImg_url("");

        List<CharacterProjection> resumeAll = new ArrayList<>();
        resumeAll.add(characterProjection);

        when(characterService.resumeAllCharacters()).thenReturn(resumeAll);
        ResponseEntity<List<CharacterProjection>> response = characterController.resumeAllCharacters();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(resumeAll, response.getBody());
    }

    @Test
    void resumeAllCharactersEmptyTest() throws SQLException {
        List<CharacterProjection> resumeAll = new ArrayList<>();

        when(characterService.resumeAllCharacters()).thenReturn(resumeAll);
        ResponseEntity<List<CharacterProjection>> response = characterController.resumeAllCharacters();
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void resumeAllCharactersTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).resumeAllCharacters();

        Assertions.assertThrows(SQLException.class, () -> characterController.resumeAllCharacters());
    }

    @Test
    void createCharacterTestCreated() throws URISyntaxException, SQLException, DataValidationException {

        CharacterModelDto characterModelDto = new CharacterModelDto();
        characterModelDto.setImgUrl("");
        characterModelDto.setName("nuevo");
        characterModelDto.setAge(10);
        characterModelDto.setWeight(2);
        characterModelDto.setStory("nuevo personaje");
        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        characterModelDto.setMovieIdList(idList);

        MovieModel mockedMovie = new MovieModel(1, 1, "", "tittle", new Date());
        when(characterService.createCharacter(characterModelDto)).thenReturn(1);
        when(movieService.findById(1)).thenReturn(Optional.of(mockedMovie));

        List<MovieModel> movieModelList = new ArrayList<>();
        movieModelList.add(mockedMovie);

        characterModelDto.setIdCharacter(1);
        characterModelDto.setMovieModelList(movieModelList);

        ResponseEntity<CharacterModelDto> responseEntity = ResponseEntity.created(new URI("http://localhost:8080/characters/"+1)).body(characterModelDto);
        Assertions.assertNotNull(responseEntity);
        Assertions.assertEquals(responseEntity, characterController.createCharacter(characterModelDto));
        verify(characterService, times(1)).createCharacter(characterModelDto);
    }

    @Test
    void createCharacterTestSQLException() throws DataValidationException, SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setAge(1);
        dto.setWeight(2);
        dto.setImgUrl("");
        dto.setName("a");
        dto.setStory("a");
        when(characterService.createCharacter(dto)).thenThrow(
                new DataIntegrityViolationException("")
        );

        Assertions.assertThrows(DataValidationException.class, () -> characterController.createCharacter(dto) );
    }

    @Test
    void createCharacterTestDataValidationException() throws DataValidationException, SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setAge(-1);
        when(characterService.createCharacter(dto)).thenThrow(
                new DataValidationException("Invalid new character information")
        );

        Assertions.assertThrows(DataValidationException.class, () -> characterController.createCharacter(dto) );
    }

    @Test
    void readCharacterByIdTestOk() throws DataValidationException {
        CharacterModel characterModel = new CharacterModel();
        characterModel.setIdCharacter(1);
        characterModel.setImgUrl("");
        characterModel.setName("Manny");
        characterModel.setAge(30);
        characterModel.setWeight(500);
        characterModel.setStory("Es un Mamut lanudo, muy malhumorado");
        when(characterService.findById(1)).thenReturn(Optional.of(characterModel));

        ResponseEntity<?> response = characterController.readCharacterById(1);

        Assertions.assertEquals(characterModel, response.getBody());
        Assertions.assertNotNull(response);
        verify(characterService, times(1)).findById(1);
    }

    @Test
    void readCharacterByIdTestNoContent() throws DataValidationException {
        when(characterService.findById(1)).thenReturn(Optional.empty());
        ResponseEntity<?> response = characterController.readCharacterById(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void readCharacterByIdTestException() {
        Assertions.assertThrows(DataValidationException.class, () -> characterController.readCharacterById(-1) );
    }

    @Test
    void updateCharacterTestAccepted() throws DataValidationException, SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setIdCharacter(1);
        dto.setAge(1);
        dto.setWeight(2);
        dto.setImgUrl("");
        dto.setName("a");
        dto.setStory("a");

        doNothing().when(characterService).updateCharacter(dto);

        ResponseEntity<?> responseEntity = characterController.updateCharacter(dto);

        Assertions.assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(characterService, times(1)).updateCharacter(dto);
    }

    @Test
    void updateCharacterTestJpaSystemException() throws SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setIdCharacter(1);
        dto.setAge(1);
        dto.setWeight(2);
        dto.setImgUrl("");
        dto.setName("a");
        dto.setStory("a");

        doThrow(new JpaSystemException(new RuntimeException(new SQLException()))).when(characterService).updateCharacter(dto);

        Assertions.assertThrows(SQLException.class, () -> characterController.updateCharacter(dto) );

    }

    @Test
    void updateCharacterTestDataValidationException() {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setIdCharacter(-1);
        dto.setAge(1);
        dto.setWeight(2);
        dto.setImgUrl("dasda");
        dto.setName("a");
        dto.setStory("a");

        Assertions.assertThrows(
                DataValidationException.class,
                () -> characterController.updateCharacter(dto)
        );

        CharacterModelDto dto2 = new CharacterModelDto();
        dto2.setIdCharacter(-1);
        dto2.setAge(-1);
        dto2.setWeight(-2);
        dto2.setImgUrl("");
        dto2.setName("a");
        dto2.setStory("a");

        Assertions.assertThrows(
                DataValidationException.class,
                () -> characterController.updateCharacter(dto2)
        );
    }

    @Test
    void deleteCharacterByIdTestOk() throws DataValidationException, SQLException, ElementDoesNotExistException {
        doNothing().when(characterService).deleteCharacterById(1);
        ResponseEntity<?> response = characterController.deleteCharacterById(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(characterService, times(1)).deleteCharacterById(1);
    }

    @Test
    void deleteCharacterByIdTestJpaSystemException() {

        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).deleteCharacterById(200);

        Assertions.assertThrows(SQLException.class, () -> characterController.deleteCharacterById(200) );
    }

    @Test
    void deleteCharacterByIdTestEmptyResultDataAccessException() {

        doThrow(new EmptyResultDataAccessException(1))
                .when(characterService).deleteCharacterById(200);

        Assertions.assertThrows(ElementDoesNotExistException.class, () -> characterController.deleteCharacterById(200) );
    }

    @Test
    void deleteCharacterByIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> characterController.deleteCharacterById(-1) );
    }

    @Test
    void findByNameTestOk() throws DataValidationException, SQLException {
        CharacterModel characterModel = new CharacterModel(1,10,10,"","name","story");
        when(characterService.findByName("name")).thenReturn(characterModel);

        ResponseEntity<CharacterModel> response = characterController.findByName("name");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(characterModel, response.getBody());
        verify(characterService, times(1)).findByName("name");
    }

    @Test
    void findByNameTestNoContent() throws DataValidationException, SQLException {
        when(characterService.findByName("name")).thenReturn(null);

        ResponseEntity<CharacterModel> response = characterController.findByName("name");
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Assertions.assertNull(response.getBody());
        verify(characterService, times(1)).findByName("name");
    }

    @Test
    void findByNameTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class,
                () -> characterController.findByName(""));
    }

    @Test
    void findByNameTestJpaSystemException() {

        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).findByName("hola");

        Assertions.assertThrows(SQLException.class, () -> characterController.findByName("hola"));
        verify(characterService, times(1)).findByName("hola");
    }

    @Test
    void findCharactersByAgeTestOk() throws DataValidationException, SQLException {
        List<CharacterModel> mock = new ArrayList<>();
        CharacterModel mockModel = new CharacterModel(1,10,10,"","name","story");
        mock.add(mockModel);

        when(characterService.findCharactersByAge(10)).thenReturn(mock);
        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByAge(10);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(mock, response.getBody());
        verify(characterService, times(1)).findCharactersByAge(10);
    }

    @Test
    void findCharactersByAgeTestNoContent() throws DataValidationException, SQLException {
        List<CharacterModel> mock = new ArrayList<>();

        when(characterService.findCharactersByAge(1000)).thenReturn(mock);
        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByAge(1000);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(characterService, times(1)).findCharactersByAge(1000);
    }

    @Test
    void findCharactersByAgeTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).findCharactersByAge(10);
        Assertions.assertThrows(SQLException.class, () -> characterController.findCharactersByAge(10));
        verify(characterService, times(1)).findCharactersByAge(10);
    }

    @Test
    void findCharactersByAgeTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> characterController.findCharactersByAge(0));
    }

    @Test
    void findCharactersByWeightTestOk() throws DataValidationException, SQLException {
        List<CharacterModel> mockList = new ArrayList<>();
        CharacterModel mockModel = new CharacterModel(1,10,10,"","name","story");
        mockList.add(mockModel);

        when(characterService.findByWeight(10)).thenReturn(mockList);

        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByWeight(10);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(mockList, response.getBody());
        verify(characterService, times(1)).findByWeight(10);
    }

    @Test
    void findCharactersByWeightTestNoContent() throws DataValidationException, SQLException {
        List<CharacterModel> mockList = new ArrayList<>();

        when(characterService.findByWeight(10)).thenReturn(mockList);

        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByWeight(10);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(characterService, times(1)).findByWeight(10);
    }

    @Test
    void findCharactersByWeightTestJpaSystemException()  {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).findByWeight(10);

        Assertions.assertThrows(SQLException.class, () -> characterController.findCharactersByWeight(10));
        verify(characterService, times(1)).findByWeight(10);
    }

    @Test
    void findCharactersByWeightTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> characterController.findCharactersByWeight(0));
    }

    @Test
    void findCharactersByMovieIdTestOk() throws DataValidationException, SQLException {
        List<CharacterModel> mockList = new ArrayList<>();
        CharacterModel mockModel = new CharacterModel(1,10,10,"","name","story");
        mockList.add(mockModel);

        when(characterService.findCharactersByMovieId(1)).thenReturn(mockList);

        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByMovieId(1);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(mockList, response.getBody());
        verify(characterService, times(1)).findCharactersByMovieId(1);
    }

    @Test
    void findCharactersByMovieIdTestNoContent() throws DataValidationException, SQLException {
        List<CharacterModel> mockList = new ArrayList<>();

        when(characterService.findCharactersByMovieId(1)).thenReturn(mockList);

        ResponseEntity<List<CharacterModel>> response = characterController.findCharactersByMovieId(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(characterService, times(1)).findCharactersByMovieId(1);
    }

    @Test
    void findCharactersByMovieIdTestJpaSystemException() {
        doThrow(new JpaSystemException(new RuntimeException(new SQLException())))
                .when(characterService).findCharactersByMovieId(1);

        Assertions.assertThrows(SQLException.class, () -> characterController.findCharactersByMovieId(1) );
        verify(characterService, times(1)).findCharactersByMovieId(1);
    }

    @Test
    void findCharactersByMovieIdTestDataValidationException() {
        Assertions.assertThrows(DataValidationException.class, () -> characterController.findCharactersByMovieId(0) );
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

package com.disneymovie.disneyJava.services;

import com.disneymovie.disneyJava.dtos.CharacterModelDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.projections.CharacterProjection;
import com.disneymovie.disneyJava.repositories.CharacterRepository;
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
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class CharacterServiceTest {
    AutoCloseable openMocks;

    private CharacterService characterService;
    private CharacterProjection characterProjection;

    @Mock
    CharacterRepository characterRepository;

    @BeforeEach
    void setUp() {
        //initMocks(this);
        openMocks = MockitoAnnotations.openMocks(this);

        characterService = new CharacterService(characterRepository);

        ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
        this.characterProjection = factory.createProjection(CharacterProjection.class);
    }

    @Test
    void resumeAllCharacters() {
        characterProjection.setImg_url("");
        List<CharacterProjection> list = new ArrayList<>();
        list.add(characterProjection);

        when(characterRepository.resumeAllCharacters()).thenReturn(list);

        List<CharacterProjection> response = characterService.resumeAllCharacters();
        Assertions.assertEquals(response, list);
        Assertions.assertNotNull(response);
        verify(characterRepository, times(1)).resumeAllCharacters();
    }

    @Test
    void createCharacterTest() throws SQLException, DataValidationException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setImgUrl("a");
        dto.setName("n");
        dto.setAge(1);
        dto.setWeight(1);
        dto.setStory("s");
        List<Integer> idList = new ArrayList<>();
        idList.add(1);
        dto.setMovieIdList(idList);

        when(characterRepository.createCharacter("a","n",1,1,"s"))
                .thenReturn(1);
        Integer id = characterService.createCharacter(dto);

        Assertions.assertEquals(1,id);
        doNothing().when(characterRepository).addMoviesToCharacter(1,1);
        verify(characterRepository, times(1)).createCharacter("a","n",1,1,"s");
    }

    @Test
    void createCharacterTestDataValidationException() throws SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setImgUrl("a");

        Assertions.assertThrows(DataValidationException.class, () -> characterService.createCharacter(dto));
    }

    @Test
    void updateCharacterTest() throws SQLException {
        CharacterModelDto dto = new CharacterModelDto();
        dto.setIdCharacter(1);
        dto.setImgUrl("u");
        dto.setName("n");
        dto.setAge(1);
        dto.setWeight(2);
        dto.setStory("s");

        doNothing().when(characterRepository).updateCharacter(
                1,"u","n",1,2,"s"
        );

        characterService.updateCharacter(dto);

        verify(characterRepository, times(1)).updateCharacter(1,"u","n",1,2,"s");
    }

    @Test
    void deleteCharacterByIdTest() {
        doNothing().when(characterRepository).deleteById(1);
        characterService.deleteCharacterById(1);
        verify(characterRepository, times(1)).deleteById(1);
    }

    @Test
    void findByIdTest() {
        CharacterModel model = new CharacterModel();
        when(characterRepository.findById(1)).thenReturn(Optional.of(model));

        Optional<CharacterModel> response = characterService.findById(1);

        //noinspection OptionalGetWithoutIsPresent
        Assertions.assertEquals(model, response.get());
    }

    @Test
    void findByNameTest() {
        CharacterModel model = new CharacterModel();
        when(characterRepository.findByName("1")).thenReturn(model);

        CharacterModel response = characterService.findByName("1");

        Assertions.assertEquals(model, response);
    }

    @Test
    void findCharactersByAgeTest() {
        List<CharacterModel> list = new ArrayList<>();
        CharacterModel model = new CharacterModel();
        model.setStory("s");
        list.add(model);
        when(characterRepository.findByAge(1)).thenReturn(list);

        List<CharacterModel> response = characterService.findCharactersByAge(1);

        Assertions.assertEquals(list, response);
    }

    @Test
    void findByWeightTest() {
        List<CharacterModel> list = new ArrayList<>();
        CharacterModel model = new CharacterModel();
        model.setStory("s");
        list.add(model);
        when(characterRepository.findByWeight(1)).thenReturn(list);

        List<CharacterModel> response = characterService.findByWeight(1);

        Assertions.assertEquals(list, response);
    }

    @Test
    void findCharactersByMovieIdTest() {
        List<CharacterModel> list = new ArrayList<>();
        CharacterModel model = new CharacterModel();
        model.setStory("s");
        list.add(model);
        when(characterRepository.findByMovieId(1)).thenReturn(list);

        List<CharacterModel> response = characterService.findCharactersByMovieId(1);

        Assertions.assertEquals(list, response);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }
}

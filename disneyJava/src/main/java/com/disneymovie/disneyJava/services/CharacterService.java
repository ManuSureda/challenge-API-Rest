package com.disneymovie.disneyJava.services;

import com.disneymovie.disneyJava.dtos.CharacterModelDto;
import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.projections.CharacterProjection;
import com.disneymovie.disneyJava.repositories.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class CharacterService {

    @Autowired
    private final CharacterRepository characterRepository;

    public CharacterService(CharacterRepository characterRepository) { this.characterRepository = characterRepository; }

    public List<CharacterProjection> resumeAllCharacters() throws JpaSystemException {
        return this.characterRepository.resumeAllCharacters();
    }

    public Integer createCharacter(CharacterModelDto newCharacter) throws JpaSystemException, SQLException, DataValidationException {

        Integer newIdCharacter = characterRepository.createCharacter(
                newCharacter.getImgUrl(),
                newCharacter.getName(),
                newCharacter.getAge(),
                newCharacter.getWeight(),
                newCharacter.getStory()
        );

        if (newIdCharacter > 0) {
            if (!newCharacter.getMovieIdList().isEmpty()) {
                for (Integer movieId: newCharacter.getMovieIdList()) {
                    addMoviesToCharacter((Integer) movieId,newIdCharacter);
                }
            }
        } else {
            throw new DataValidationException("Something went wrong with the new character ID");
        }

        return newIdCharacter;
    }

    public void addMoviesToCharacter(final Integer movieId,final Integer newIdCharacter) throws SQLException {
        characterRepository.addMoviesToCharacter(movieId,newIdCharacter);
    }

    public void updateCharacter(CharacterModelDto modifiedCharacter) throws JpaSystemException, SQLException {
        characterRepository.updateCharacter(
                modifiedCharacter.getIdCharacter(),
                modifiedCharacter.getImgUrl(),
                modifiedCharacter.getName(),
                modifiedCharacter.getAge(),
                modifiedCharacter.getWeight(),
                modifiedCharacter.getStory()
        );
    }

    public void deleteCharacterById(Integer id) throws JpaSystemException {
        characterRepository.deleteById(id);
    }

    public Optional<CharacterModel> findById(Integer id) {
        return characterRepository.findById(id);
    }

    public CharacterModel findByName(String name) {
        return characterRepository.findByName(name);
    }

    public List<CharacterModel> findCharactersByAge(Integer age) {
        return characterRepository.findByAge(age);
    }

    public List<CharacterModel> findByWeight(Integer weight) {
        return characterRepository.findByWeight(weight);
    }

    public List<CharacterModel> findCharactersByMovieId(Integer idMovie) {
        return characterRepository.findByMovieId(idMovie);
    }
}

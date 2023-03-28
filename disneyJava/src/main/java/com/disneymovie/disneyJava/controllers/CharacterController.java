package com.disneymovie.disneyJava.controllers;

import com.disneymovie.disneyJava.exceptions.DataValidationException;
import com.disneymovie.disneyJava.dtos.CharacterModelDto;
import com.disneymovie.disneyJava.exceptions.ElementDoesNotExistException;
import com.disneymovie.disneyJava.models.CharacterModel;
import com.disneymovie.disneyJava.models.MovieModel;
import com.disneymovie.disneyJava.projections.CharacterProjection;
import com.disneymovie.disneyJava.services.CharacterService;
import com.disneymovie.disneyJava.services.MovieService;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/characters")
public class CharacterController {
    @Autowired
    private final CharacterService characterService;
    @Autowired
    private final MovieService movieService;

    public CharacterController(CharacterService characterService, MovieService movieService) {
        this.characterService = characterService;
        this.movieService = movieService;
    }

//    3. Listado de Personajes
//    El listado deberá mostrar:
//      •	Imagen
//      •	Nombre
//    El endpoint deberá ser:
//            •	/characters
    @GetMapping()
    public ResponseEntity<List<CharacterProjection>> resumeAllCharacters() throws SQLException {
        try {
            List<CharacterProjection> response = this.characterService.resumeAllCharacters();
            if (response.isEmpty()){
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok().body(response);
            }
        } catch (JpaSystemException e) {
//            el caso mas probable es que el resultado no se pueda mapear con un CharacterProjection
            throw new SQLException(e.getCause().getCause().getMessage());
        }
    }

//    4. Creacion, Edicion y Eliminacion de Personajes (CRUD)
    @PostMapping()
    public ResponseEntity<CharacterModelDto> createCharacter(@RequestBody final CharacterModelDto newCharacterDto) throws URISyntaxException, SQLException, DataValidationException {
        if (newCharacterDto.isValid()) {
            try {
                Integer newId = characterService.createCharacter(newCharacterDto);
                newCharacterDto.setIdCharacter(newId);

                ArrayList<MovieModel> movieModels = new ArrayList<>();

                for (Integer movieId: newCharacterDto.getMovieIdList() ) {
                    Optional<MovieModel> movie = movieService.findById(movieId);
                    if (movie.isPresent()) {
                        movieModels.add(movie.get());
                    }
                }
                newCharacterDto.setMovieIdList(null);
                newCharacterDto.setMovieModelList(movieModels);
                return ResponseEntity.created(new URI("http://localhost:8080/characters/"+newId)).body(newCharacterDto);
            } catch (DataIntegrityViolationException e) {
                throw new DataValidationException("The character was created, but some of the provided id movies are not present in the database.");
            }
        } else {
            throw new DataValidationException("Invalid new character information");
        }
    }
// READ
    @GetMapping("/{id}")
    public ResponseEntity<?> readCharacterById(@PathVariable("id") final Integer id) throws DataValidationException {
        if (id > 0) {
            Optional<CharacterModel> response = characterService.findById(id);
            if (response.isPresent()) {
                return ResponseEntity.ok().body(response.get());
            } else {
                return ResponseEntity.noContent().build();
            }
        } else {
            throw new DataValidationException("Id must be positive");
        }
    }

//edicion
    @PutMapping("/")
    public ResponseEntity<?> updateCharacter(@RequestBody final CharacterModelDto modifiedCharacter) throws SQLException, DataValidationException {
        if (modifiedCharacter.isValid()) {
            if (modifiedCharacter.getIdCharacter() > 0) {
                try {
                    characterService.updateCharacter(modifiedCharacter);
                    return ResponseEntity.accepted().build();
                } catch (JpaSystemException e) {
                    throw new SQLException(e.getCause().getCause().getMessage());
                }
            } else {
                throw new DataValidationException("ID of the Character most be positive");
            }
        } else {
            throw new DataValidationException("Modified character's data are wrong, void, null or empty");
        }
    }
//eliminacion
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCharacterById(@PathVariable("id") final Integer id) throws DataValidationException, SQLException, ElementDoesNotExistException {
        if (id > 0) {
            try {
                characterService.deleteCharacterById(id);
                return ResponseEntity.ok().build();
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            } catch (EmptyResultDataAccessException e) {
                //el 204 no content ya se encarga solo
                throw new ElementDoesNotExistException("There is no character with that ID");
            }
        } else {
            throw new DataValidationException("Character ID must be positive");
        }
    }

//    FIN CRUD

//    6. Busqueda de Personaejs
//    Deberán Prmitir buscar por nombre, edad, peso o peliculas/series en las que participo.
//    Para especificar el termino de búsqueda o filtros se deberán enviar como parámetros de query:
//•	GET /characers?name=nombre
    @GetMapping(params = {"name"})
    public ResponseEntity<CharacterModel> findByName(@RequestParam final String name) throws DataValidationException, SQLException {
        if (!StringUtils.isBlank(name)) {
            try {
                CharacterModel response = characterService.findByName(name); // solo puede retornar un character ya que name es unique
                if (response != null) {
                    return ResponseEntity.ok().body(response);
                } else {
                    return ResponseEntity.noContent().build();
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Name cant be: null, empty or blank");
        }
    }
//•	GET /characers?age=edad
    @GetMapping(params = {"age"})
    public ResponseEntity<List<CharacterModel>> findCharactersByAge(@RequestParam final Integer age) throws DataValidationException, SQLException {
        if (age > 0) {
            try {
                List<CharacterModel> response = characterService.findCharactersByAge(age);
                if (response.isEmpty()) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok().body(response);
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Age must be positive");
        }
    }
    //•	GET /characers?weight=peso
    @GetMapping(params = {"weight"})
    public ResponseEntity<List<CharacterModel>> findCharactersByWeight(@RequestParam final Integer weight) throws DataValidationException, SQLException {
        if (weight > 0) {
            try {
                List<CharacterModel> response = characterService.findByWeight(weight);
                if (response.isEmpty()) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok().body(response);
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("Weight must be positive");
        }
    }

//•	GET /characers?movies=idMovie
    @GetMapping(params = {"idMovie"})
    public ResponseEntity<List<CharacterModel>> findCharactersByMovieId(@RequestParam final Integer idMovie) throws DataValidationException, SQLException {
        if (idMovie > 0) {
            try {
                List<CharacterModel> response = characterService.findCharactersByMovieId(idMovie);
                if (response.isEmpty()) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.ok().body(response);
                }
            } catch (JpaSystemException e) {
                throw new SQLException(e.getCause().getCause().getMessage());
            }
        } else {
            throw new DataValidationException("The Movie ID must be positive");
        }
    }
}

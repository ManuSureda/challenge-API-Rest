import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { CharacterModel } from 'src/app/models/character-model';
import { CharacterModelDto } from 'src/app/dtos/character-model-dto';
import { CharacterService } from 'src/app/services/character.service';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomValidator } from 'src/app/commons/custom-validator';
import { MovieModel } from 'src/app/models/movie-model';
import { MovieService } from 'src/app/services/movie.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-character',
  templateUrl: './character.component.html',
  styleUrls: ['./character.component.css']
})
export class CharacterComponent implements OnInit {
  
  readonly defaultCharacterPoster : string = "assets/defaultCharacter.jpg";

  charactersResumeArray : Array<CharacterModel> = [];
  character = undefined;
  
  moviesByCharacterArray : Array<MovieModel> = [];
  
  selectedOption = "";
  alertMessage = "";
  flagUpdate = false;
  flagCreate = false;

  updateForm = new FormGroup({
    uName:    new FormControl('', [CustomValidator.hasLeadingSpace()]),
    uAge:     new FormControl('', ),
    uWeight:  new FormControl('', ),
    uImgUrl:  new FormControl('', [CustomValidator.hasLeadingSpace()]),
    uStory:   new FormControl('', [CustomValidator.hasLeadingSpace()]) 
  });

  createForm = new FormGroup({
    cName:   new FormControl('',[Validators.required, CustomValidator.hasLeadingSpace()],[CustomValidator.characterNameExist(this.characterService)]),
    cAge:    new FormControl('',[Validators.required, Validators.min(1), CustomValidator.numbersOnly(), CustomValidator.hasLeadingSpace()]),
    cWeight: new FormControl('',[Validators.required, Validators.min(1), CustomValidator.numbersOnly(), CustomValidator.hasLeadingSpace()]),
    cImgUrl: new FormControl('assets/defaultCharacter.jpg', [CustomValidator.hasLeadingSpace()]),
    cStory:  new FormControl('',[Validators.required, CustomValidator.hasLeadingSpace()])
  });

  constructor(private characterService : CharacterService, private movieService : MovieService, private router: Router, private route: ActivatedRoute) { }

  get uName()   { return this.updateForm.get('uName');   }
  get uAge()    { return this.updateForm.get('uAge');    }
  get uWeight() { return this.updateForm.get('uWeight'); }
  get uImgUrl() { return this.updateForm.get('uImgUrl'); }
  get uStory()  { return this.updateForm.get('uStory');  }

  get cAge()    { return this.createForm.get('cAge');    }    
  get cWeight() { return this.createForm.get('cWeight'); }     
  get cImgUrl() { return this.createForm.get('cImgUrl'); }     
  get cName()   { return this.createForm.get('cName');   }     
  get cStory()  { return this.createForm.get('cStory');  }    
  
  ngOnInit(): void {
    this.alertMessage = "";
    this.selectedOption = '';
    this.character = undefined;
    this.flagCreate = false;
    this.flagUpdate = false;

    const state = window.history.state;
    if (state && state.characterName) {
      this.showCharacterByName(state.characterName);
    } else { 
      this.showAllCharacters();
    }
  }

  showAllCharacters(): void {
    this.character = undefined;
    this.selectedOption = "";
    this.alertMessage = "";
    this.flagUpdate = false;
    this.flagCreate = false;

    const promise = this.characterService.resumeAllCharacters();

    promise
      .then(response => {
        response.forEach(element => {
          let character = new CharacterModel();
          if (element.img_url === '') {
            character.setImgUrl(this.defaultCharacterPoster);
          } else {
            character.setImgUrl(element.img_url);
          }
          character.setName(element.name);

          this.charactersResumeArray.push(character);
        });
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
  }

  showCharacterByName(name: string): void {
    this.selectedOption = '';
    this.flagCreate = false;
    this.alertMessage = "";
    this.character = undefined;

    this.charactersResumeArray = [];
    
    const promise = this.characterService.findByName(name);

    promise
      .then(response => {
        if (response != null) {
          let char = new CharacterModel();
  
          char.setIdCharacter(response.idCharacter);
          char.setAge(response.age);
          if (response.imgUrl === '') {
            char.setImgUrl(this.defaultCharacterPoster);
          } else {
            char.setImgUrl(response.imgUrl);
          }
          char.setName(response.name);
          char.setStory(response.story);
          char.setWeight(response.weight);
  
          this.getMoviesByCharacterId(char.getIdCharacter());
  
          this.character = char;
        } else {
          this.alertMessage = 'There is no character with the name: ' + name;
        }
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
  }

  getMoviesByCharacterId(characterId : number): void {
    this.alertMessage = "";

    const promise = this.movieService.getMoviesByCharacterId(characterId);

    promise
      .then(response => {
        this.moviesByCharacterArray = [];
        response.forEach(element => {
          let movie : MovieModel = new MovieModel(); 
          
          movie.setIdMovie(element.idMovie);
          if (element.imgUrl === '') {
            movie.setImgUrl("assets/defaultMovie.jpg");
          } else {
            movie.setImgUrl(element.imgUrl);
          }
          movie.setReleaseDate(new Date(element.releaseDate));
          movie.setTittle(element.tittle);

          this.moviesByCharacterArray.push(movie);
        });
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
  }

  goToMovie(idMovie : number): void {    
    this.router.navigate(['/movies'], { state: { id: idMovie } });
  }

  updateCharacter(): void {
    this.alertMessage = "";
    this.flagUpdate = true;
    this.flagCreate = false;
  }

  updateSubmit(): void {
    this.selectedOption = '';
    this.alertMessage = '';

    if (this.uName.value === '' && this.uAge.value === '' && this.uWeight.value === '' && this.uImgUrl.value === '' && this.uStory.value === '') {
      console.log("no se modifico nada");
      
      this.flagUpdate = false;
    } else {
      let dto : CharacterModelDto = new CharacterModelDto();
      dto.setIdCharacter(this.character.getIdCharacter());
      dto.setName   (this.uName.value   === '' ? this.character.getName()   : this.uName.value);
      dto.setAge    (this.uAge.value    === '' ? this.character.getAge()    : this.uAge.value);
      dto.setWeight (this.uWeight.value === '' ? this.character.getWeight() : this.uWeight.value);
      dto.setImgUrl (this.uImgUrl.value === '' ? this.character.getImgUrl() : this.uImgUrl.value);
      dto.setStory  (this.uStory.value  === '' ? this.character.getStory()  : this.uStory.value);
      
      const promise = this.characterService.updateCharacter(dto);
      
      promise
        .then(response => {
          this.flagUpdate = false;
          this.showCharacterByName(dto.getName());
        })
        .catch(err => {
          console.log(err);
          if (err instanceof HttpErrorResponse) {
            if (err.status === 0) {
              this.alertMessage = "Sorry, it seems like the server is down, please try again later";
            } else {
              this.alertMessage = err.error.description;
            }
          } 
        });
    }
  }

  cancelUpdate(): void {
    this.flagUpdate = false;
  }

  createCharacter(): void {
    this.charactersResumeArray = [];
    this.character = undefined;
    this.flagUpdate = false;
    this.alertMessage = "";
    this.flagCreate = true;
  }

  createSubmit(): void {
    this.alertMessage = "";

    let dto : CharacterModelDto = new CharacterModelDto();

    dto.setName(this.cName.value);
    dto.setAge(this.cAge.value);
    dto.setWeight(this.cWeight.value);
    dto.setStory(this.cStory.value);
    dto.setImgUrl(this.cImgUrl.value === '' ? this.defaultCharacterPoster : this.cImgUrl.value);
    
    const promise = this.characterService.createCharacter(dto);

    promise
      .then(response => {
        this.flagCreate = false;
        this.showCharacterByName(response.name);
        
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        } 
      });
  }

  deleteCharacterById(id: number): void {
    this.alertMessage = "";

    const promise = this.characterService.deleteCharacterById(id);

    promise
      .then(response => {
        this.showAllCharacters();
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
  }

  onSelectChange(event: any): void {
    this.selectedOption = event.target.value;
  }

  searchByAge(age : number): void {
    this.flagCreate = false;
    this.flagUpdate = false;
    this.character = undefined;
    this.selectedOption = '';
    const promise = this.characterService.findCharactersByAge(age);

    promise
      .then(response => {
        if (response != null) {
          this.charactersResumeArray = [];
          
          response.forEach(element => {
            let character = new CharacterModel();
            if (element.imgUrl === '') {
              character.setImgUrl(this.defaultCharacterPoster);
            } else {
              character.setImgUrl(element.imgUrl);
            }
            character.setName(element.name);
  
            this.charactersResumeArray.push(character);
          });          
        } else {
          this.alertMessage = "There are no characters with that age: " + age;
        }
        
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
    
  } 

  searchByWeight(weight : number): void {
    this.flagCreate = false;
    this.flagUpdate = false;
    this.character = undefined;
    this.selectedOption = '';
    const promise = this.characterService.findCharactersByWeight(weight);

    promise
      .then(response => {
        if (response != null) {
          this.charactersResumeArray = [];
          response.forEach(element => {
            let character = new CharacterModel();
            if (element.imgUrl === '') {
              character.setImgUrl(this.defaultCharacterPoster);
            } else {
              character.setImgUrl(element.imgUrl);
            }
            character.setName(element.name);
  
            this.charactersResumeArray.push(character);
          });          
        } else {
          this.alertMessage = "There are no characters with that weight: " + weight;
        }
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
    
  } 

  searchByMovie(movie : string): void {
    this.flagCreate = false;
    this.flagUpdate = false;
    this.character = undefined;
    this.selectedOption = '';

    const moviePromise = this.movieService.findByTittle(movie);

    moviePromise
      .then(response => {
        if (response != null) {
          const promise = this.characterService.findCharactersByMovieId(response.idMovie);
          
          promise
            .then(response => {
              if (response != null) {
                this.charactersResumeArray = [];

                response.forEach(element => {
                  console.log(element);
                  
                  let character = new CharacterModel();
                  if (element.imgUrl === '') {
                    character.setImgUrl(this.defaultCharacterPoster);
                  } else {
                    character.setImgUrl(element.imgUrl);
                  }
                  character.setName(element.name);
        
                  this.charactersResumeArray.push(character);
                });          
              } else {
                this.alertMessage = "Apparently this movie has been saved without any character: " + movie;
              }
            })
            .catch(err => {
              console.log(err);
              if (err instanceof HttpErrorResponse) {
                if (err.status === 0) {
                  this.alertMessage = "Sorry, it seems like the server is down, please try again later";
                } else {
                  this.alertMessage = err.error.description;
                }
              }
            });
        } else {
          this.alertMessage = "There are no movies with that title: " + movie;
        }
      })
      .catch(err => {
        console.log(err);
        if (err instanceof HttpErrorResponse) {
          if (err.status === 0) {
            this.alertMessage = "Sorry, it seems like the server is down, please try again later";
          } else {
            this.alertMessage = err.error.description;
          }
        }
      });
  } 

}


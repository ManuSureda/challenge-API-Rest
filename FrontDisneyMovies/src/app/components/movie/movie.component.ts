import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomValidator } from 'src/app/commons/custom-validator';
import { MovieModelDto } from 'src/app/dtos/movie-model-dto';
import { MovieGenreModel } from 'src/app/models/movie-genre-model';
import { MovieModel } from 'src/app/models/movie-model';
import { CharacterService } from 'src/app/services/character.service';
import { MovieService } from 'src/app/services/movie.service';

@Component({
  selector: 'app-movie',
  templateUrl: './movie.component.html',
  styleUrls: ['./movie.component.css']
})
export class MovieComponent implements OnInit {

  readonly defaultMoviePoster : string = "assets/defaultMovie.jpg";
  readonly defaultCharacterPoster : string = "assets/defaultCharacter.jpg";

  resumArray : Array<MovieModel> = [];
  movieModelDto : MovieModelDto = undefined;
  genresList : Array<any> = [];

  selectedOption = "";
  alertMessage = "";
  flagUpdate = false;
  flagCreate = false;
  flagAddCharacter = false;

  updateForm = new FormGroup({
    uTittle:       new FormControl('', [CustomValidator.hasLeadingSpace()], [CustomValidator.movieTitleExist(this.movieService)]),
    uReleaseDate:  new FormControl('', [CustomValidator.hasLeadingSpace()]),
    uScore:        new FormControl('', [Validators.min(1), Validators.max(5)]),
    uImgUrl:       new FormControl('', [CustomValidator.hasLeadingSpace()]),
    uGenresIdList: new FormControl([], )
  });

  createForm = new FormGroup({                                                                
   cTittle:       new FormControl('',[Validators.required, CustomValidator.hasLeadingSpace()],[CustomValidator.movieTitleExist(this.movieService)]),
   cReleaseDate:  new FormControl('',[Validators.required]),
   cScore:        new FormControl('',[Validators.required, Validators.min(1), Validators.max(5), CustomValidator.numbersOnly(), CustomValidator.hasLeadingSpace()]),
   cImgUrl:       new FormControl('assets/defaultMovie.jpg', [CustomValidator.hasLeadingSpace()]),
   cGenresIdList: new FormControl([]),
   cCharactersIdList: new FormControl([])
  });// characters are added later throw update

  addCharacterForm = new FormGroup({
    aName: new FormControl('', [Validators.required, CustomValidator.hasLeadingSpace], [CustomValidator.characterNameDontExist(this.characterService)])
  });
  
  constructor(private movieService : MovieService, private characterService : CharacterService, private router: Router, private route: ActivatedRoute) { }
  
  get uTittle()           { return this.updateForm.get('uTittle');           }   
  get uReleaseDate()      { return this.updateForm.get('uReleaseDate');      }
  get uScore()            { return this.updateForm.get('uScore');            }
  get uImgUrl()           { return this.updateForm.get('uImgUrl');           }
  get uGenresIdList()     { return this.updateForm.get('uGenresIdList');     }

  get cTittle()           { return this.createForm.get('cTittle');           }   
  get cReleaseDate()      { return this.createForm.get('cReleaseDate');      }
  get cScore()            { return this.createForm.get('cScore');            }
  get cImgUrl()           { return this.createForm.get('cImgUrl');           }
  get cGenresIdList()     { return this.createForm.get('cGenresIdList');     }
  get cCharactersIdList() { return this.createForm.get('cCharactersIdList'); }
  
  get aName()             { return this.addCharacterForm.get('aName');       }

  ngOnInit(): void {
    this.resumArray = [];
    this.movieModelDto = undefined;
    this.selectedOption = "";
    this.alertMessage = "";
    this.flagUpdate = false;
    this.flagCreate = false;

    const genresPromise = this.movieService.getAllMovieGenres();
    genresPromise
      .then(response => {
        this.genresList = response;
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

    const state = window.history.state;
    if (state && state.id) {
      this.showMovieById(state.id);
    } else { 
      this.showAllMovies();
    }
  }

  showAllMovies(): void {
    this.movieModelDto = undefined;
    this.selectedOption = "";
    this.alertMessage = "";
    this.flagUpdate = false;
    this.flagCreate = false;

    const promise = this.movieService.getAllMovieResume();
      
    promise
      .then(response => {
        
        response.forEach(resume => {
          let movie : MovieModel = new MovieModel(); 
          
          if (resume.img_url === '') {
            movie.setImgUrl(this.defaultMoviePoster);
          } else {
            movie.setImgUrl(resume.img_url);
          }
          const releaseDate = new Date(Date.parse(resume.release_date));
          const localReleaseDate = new Date(releaseDate.getUTCFullYear(), releaseDate.getUTCMonth(), releaseDate.getUTCDate());
          movie.setReleaseDate(localReleaseDate);
          
          movie.setTittle(resume.tittle);
          
          this.resumArray.push(movie);          
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

  showMovieByTitle(title : string): void {
    this.resumArray = [];
    this.movieModelDto = undefined;
    this.selectedOption = "";
    this.alertMessage = "";
    this.flagUpdate = false;
    this.flagCreate = false;

    const promise = this.movieService.findByTittle(title);

    promise
      .then(response => {
        if (response) {
          this.showMovieById(response.idMovie);
        } else {
          this.alertMessage = 'There is no Movie with that title: ' + title;
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

  showMovieById(idMovie : number) {
    this.alertMessage = "";
    this.selectedOption = '';
    this.movieModelDto = undefined;

    const promise = this.movieService.getMovieById(idMovie);

    promise
      .then(response => {
        
        if (response != null) {
          response.releaseDate = new Date(response.releaseDate).toLocaleDateString();
          
          this.movieModelDto = response;
        } else {
          this.alertMessage = "Sorry. It seems like there was a problem with the server answer, please try again later, or try to se another movie.";
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

  searchByGenre(genresName: string): void {
    this.movieModelDto = undefined;
    this.flagAddCharacter = false;
    this.flagCreate = false;
    this.flagUpdate = false;
    this.alertMessage = "";
    this.selectedOption = '';
    
    const found = this.genresList.find(genre => {
      return genre.genre === genresName;
    });
    
    if (found) {
      this.resumArray = [];
      const promise = this.movieService.findByGenreId(found.idMovieGenre);
      promise
        .then(response => {
          response.forEach(resume => {
            let movie : MovieModel = new MovieModel(); 
            
            resume.imgUrl === '' ? movie.setImgUrl(this.defaultMoviePoster) : movie.setImgUrl(resume.imgUrl); 
            const releaseDate = new Date(Date.parse(resume.releaseDate));
            const localReleaseDate = new Date(releaseDate.getUTCFullYear(), releaseDate.getUTCMonth(), releaseDate.getUTCDate());
            movie.setReleaseDate(localReleaseDate);
            movie.setTittle(resume.tittle);
            
            this.resumArray.push(movie);          
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
    } else {
      this.alertMessage = "There is no genre like that.";
    }
    
  }

  createMovie(): void {
    this.selectedOption = "";
    this.alertMessage = "";
    this.flagUpdate = false;
    this.flagCreate = true;
    this.resumArray = [];
    this.movieModelDto = undefined;
    this.flagAddCharacter = false;
  }

  createSubmit(): void {
    this.alertMessage = "";

    let dto : MovieModelDto = new MovieModelDto();
    
    dto.imgUrl      = this.cImgUrl.value === '' ? this.defaultMoviePoster : this.cImgUrl.value;
    dto.tittle      = this.cTittle.value;
    dto.releaseDate = this.cReleaseDate.value;    
    dto.score       = this.cScore.value;
    let genresIdList : Array<number> = [];
    this.cGenresIdList.value.forEach(element => {
      genresIdList.push(element.idMovieGenre);
    });
    dto.genresIdList = genresIdList;

    const promise = this.movieService.createMovie(dto);

    promise
      .then(response => {
        this.flagCreate = false;
        this.showMovieById(response.idMovie);
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

  addCharacterToMovie(): void {    
    this.flagCreate = false;
    this.flagUpdate = false;
    this.flagAddCharacter = true;  
  }

  addCharacterSubmit(): void {
    
    const characterPromise = this.characterService.findByName(this.aName.value);
    characterPromise
      .then(response => {
        const promise = this.movieService.addCharacterToMovie(this.movieModelDto.idMovie,response.idCharacter);
        promise
          .then(response => {
            this.flagAddCharacter = false;
            this.showMovieById(this.movieModelDto.idMovie);
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

  updateMovie(): void {
    this.flagUpdate = true;
  }

  cancelUpdate(): void {
    this.flagUpdate = false;
  }

  updateSubmit(): void {
    this.alertMessage = "";
    const found = this.uGenresIdList.value.some(genre => 
      this.movieModelDto.genres.some(
        movieGenre => movieGenre.idMovieGenre === genre.idMovieGenre
      )
    );
        
    if (found) {
      this.alertMessage = "Some of the chosen genres are already in the movie.";
      this.flagUpdate = false;
    } else {
      let dto: MovieModelDto = new MovieModelDto;
          
      let updatedGenresIdList: Array<number> = [];
      this.uGenresIdList.value.forEach(element => {
        updatedGenresIdList.push(element.idMovieGenre);
      });

      const dateString = this.uReleaseDate.value ? this.uReleaseDate.value : this.movieModelDto.releaseDate;
      if (this.uReleaseDate.value) {
        dto.releaseDate = dateString;
      } else {
        const parts = dateString.split('/');
        const year = parseInt(parts[2]);
        const month = parseInt(parts[1]) - 1;
        const day = parseInt(parts[0]);
        const date = new Date(year, month, day);
        dto.releaseDate = date;
      }
        
      dto.idMovie      = this.movieModelDto.idMovie;
      dto.tittle       = this.uTittle.value ? this.uTittle.value : this.movieModelDto.tittle;
      dto.score        = this.uScore.value  ? this.uScore.value  : this.movieModelDto.score;
      dto.imgUrl       = this.uImgUrl.value ? this.uImgUrl.value : this.movieModelDto.imgUrl;
      dto.genresIdList = updatedGenresIdList.length === 0 ? []   : updatedGenresIdList;      

      const promise = this.movieService.updateMovie(dto);

      promise
        .then(response => {
          this.flagUpdate = false;
          this.alertMessage = "";
          this.showMovieById(this.movieModelDto.idMovie);
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

  onSelectChange(event: any): void {
    this.selectedOption = event.target.value;
  }

  deleteMovieById(idMovie: number) {
    this.alertMessage = "";
    const promise = this.movieService.deleteMovieById(idMovie);

    promise
      .then(response => {
        this.showAllMovies();
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

  goToCharacter(characterName: string): void {
    this.router.navigate(['/characters'], { state: { characterName: characterName } });
  }
}

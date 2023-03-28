import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MovieModelDto } from '../dtos/movie-model-dto';

@Injectable({
  providedIn: 'root'
})
export class MovieService {

  apiUrl: string = "http://localhost:8080/api/movies";

  constructor(private http : HttpClient) { }

  getAllMovieGenres(): Promise<any> {
    return this.http.get('http://localhost:8080/api/genres')
    .toPromise();
  }

  getMoviesByCharacterId(idCharacter: number):Promise<any> {
    return this.http.get(this.apiUrl+'?idCharacter='+idCharacter)
    .toPromise();
  }

  getAllMovieResume(): Promise<any> {
    return this.http.get(this.apiUrl)
    .toPromise();
  }

  getAllMoviesAndCharacters(): Promise<any> {
    return this.http.get(this.apiUrl+'/characters')
    .toPromise();
  }

  createMovie(dto: MovieModelDto): Promise<any> {
    return this.http.post(this.apiUrl,dto)
    .toPromise();
  }

  getMovieById(idMovie: number): Promise<any> {
    return this.http.get(this.apiUrl+'?idMovie='+idMovie)
    .toPromise();
  }

  updateMovie(dto: MovieModelDto): Promise<any> {
    console.log(dto);
    
    return this.http.put(this.apiUrl+'/',dto)
    .toPromise();
  }

  deleteMovieById(id: number): Promise<any> {
    return this.http.delete(this.apiUrl+'/'+id)
    .toPromise();
  }

  findByTittle(tittle: string): Promise<any> {
    return this.http.get(this.apiUrl+'?tittle='+tittle)
    .toPromise();
  }

  findByGenreId(genreId: number): Promise<any> {
    return this.http.get(this.apiUrl+'?genreId='+genreId)
    .toPromise();
  }

  findAllByOrder(order: string): Promise<any> {
    return this.http.get(this.apiUrl+'?order='+order)
    .toPromise();
  }

  addCharacterToMovie(movieId: number, characterId: number): Promise<any> {
    return this.http.put(this.apiUrl+'?movieId='+movieId+'&characterId='+characterId,{})
    .toPromise();
  }
}

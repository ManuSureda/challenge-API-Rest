import { CharacterModel } from "../models/character-model";
import { MovieGenreModel } from "../models/movie-genre-model";

export class MovieModelDto {
    idMovie: number;
    imgUrl: String;
    tittle: String;
    releaseDate: Date;
    score: number;
    genres: Array<MovieGenreModel> = [];
    genresIdList: Array<number> = [];
    characters: Array<CharacterModel> = [];
    charactersIdList: Array<number> = [];
}

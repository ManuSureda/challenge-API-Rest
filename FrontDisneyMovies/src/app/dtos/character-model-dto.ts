import { MovieModel } from "../models/movie-model";

export class CharacterModelDto {
    private idCharacter: number;
    private age: number;
    private weight: number;
    private imgUrl: string;
    private name: string;
    private story: string;
    private movieModelList: Array<MovieModel>;
    private movieIdList: Array<number>;

    public getIdCharacter(): number {
        return this.idCharacter;
    }

    public setIdCharacter(idCharacter: number): void {
        this.idCharacter = idCharacter;
    }

    public getAge(): number {
        return this.age;
    }

    public setAge(age: number): void {
        this.age = age;
    }

    public getWeight(): number {
        return this.weight;
    }

    public setWeight(weight: number): void {
        this.weight = weight;
    }

    public getImgUrl(): string {
        return this.imgUrl;
    }

    public setImgUrl(imgUrl: string): void {
        this.imgUrl = imgUrl;
    }

    public getName(): string {
        return this.name;
    }

    public setName(name: string): void {
        this.name = name;
    }

    public getStory(): string {
        return this.story;
    }

    public setStory(story: string): void {
        this.story = story;
    }

    public getMovieModelList(): Array<MovieModel> {
        return this.movieModelList;
    }

    public setMovieModelList(movieModelList: Array<MovieModel>): void {
        this.movieModelList = movieModelList;
    }

    public getMovieIdList(): Array<number> {
        return this.movieIdList;
    }

    public setMovieIdList(movieIdList: Array<number>): void {
        this.movieIdList = movieIdList;
    }


}

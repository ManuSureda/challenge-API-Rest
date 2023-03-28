export class MovieGenreModel {
    idMovieGenre: number ;
    genre: string ;
    imgUrl: string ;

    public getIdMovieGenre(): number {
        return this.idMovieGenre;
    }

    public setIdMovieGenre(idMovieGenre: number): void {
        this.idMovieGenre = idMovieGenre;
    }

    public getGenre(): string {
        return this.genre;
    }

    public setGenre(genre: string): void {
        this.genre = genre;
    }

    public getImgUrl(): string {
        return this.imgUrl;
    }

    public setImgUrl(imgUrl: string): void {
        this.imgUrl = imgUrl;
    }


}

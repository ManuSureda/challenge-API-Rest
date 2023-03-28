export class MovieModel {
    private idMovie: number;
    private score: number;
    private imgUrl: String;
    private tittle: String;
    private releaseDate: Date;

    public getIdMovie(): number {
        return this.idMovie;
    }

    public setIdMovie(idMovie: number): void {
        this.idMovie = idMovie;
    }

    public getScore(): number {
        return this.score;
    }

    public setScore(score: number): void {
        this.score = score;
    }

    public getImgUrl(): String {
        return this.imgUrl;
    }

    public setImgUrl(imgUrl: String): void {
        this.imgUrl = imgUrl;
    }

    public getTittle(): String {
        return this.tittle;
    }

    public setTittle(tittle: String): void {
        this.tittle = tittle;
    }

    public getReleaseDate(): Date {
        return this.releaseDate;
    }

    public setReleaseDate(releaseDate: Date): void {
        this.releaseDate = releaseDate;
    }


}

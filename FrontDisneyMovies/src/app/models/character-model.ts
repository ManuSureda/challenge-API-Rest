export class CharacterModel {
    private idCharacter: number;
    private age: number;
    private weight: number;
    private imgUrl: String;
    private name: String;
    private story: String;

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

    public getImgUrl(): String {
        return this.imgUrl;
    }

    public setImgUrl(imgUrl: String): void {
        this.imgUrl = imgUrl;
    }

    public getName(): String {
        return this.name;
    }

    public setName(name: String): void {
        this.name = name;
    }

    public getStory(): String {
        return this.story;
    }

    public setStory(story: String): void {
        this.story = story;
    }


}

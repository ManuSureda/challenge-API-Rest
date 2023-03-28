package com.disneymovie.disneyJava.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "characters")
public class CharacterModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_character")
    private Integer idCharacter;
    @Column(name = "age")
    private Integer age;
    @Column(name = "weight")
    private Integer weight;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "name")
    private String name;
    @Column(name = "story")
    private String story;
}

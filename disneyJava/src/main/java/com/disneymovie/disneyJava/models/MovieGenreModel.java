package com.disneymovie.disneyJava.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "movie_genre")
public class MovieGenreModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movie_genre")
    private Integer idMovieGenre;
    @Column(name = "genre")
    private String genre;
    @Column(name = "img_url")
    private String imgUrl;
}

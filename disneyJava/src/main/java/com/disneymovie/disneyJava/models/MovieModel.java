package com.disneymovie.disneyJava.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "movie")
public class MovieModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movie")
    private int idMovie;
    @Column(name = "score")
    private int score;
    @Column(name = "img_url")
    private String imgUrl;
    @Column(name = "tittle")
    private String tittle;
    @Column(name = "release_date")
    private Date releaseDate;
}

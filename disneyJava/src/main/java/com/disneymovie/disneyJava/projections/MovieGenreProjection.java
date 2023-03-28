package com.disneymovie.disneyJava.projections;

public interface MovieGenreProjection {
    Integer getId_movie_genre();
    String getGenre();
    String getImg_url();
    Integer getId_movie();

    void setId_movie_genre(Integer Id_movie_genre);
    void setGenre(String Genre);
    void setImg_url(String Img_url);
    void setId_movie(Integer Id_movie);
}

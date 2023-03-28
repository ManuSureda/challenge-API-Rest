package com.disneymovie.disneyJava.projections;

import com.fasterxml.jackson.annotation.JsonInclude;

public interface CharacterProjection {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer getId_character();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer getAge();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer getWeight();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getImg_url();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getName();
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String getStory();

    void setId_character(Integer id_character);
    void setAge(Integer age);
    void setWeight(Integer weight);
    void setImg_url(String img_url);
    void setName(String name);
    void setStory(String story);
}

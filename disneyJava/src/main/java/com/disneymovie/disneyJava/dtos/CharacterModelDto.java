package com.disneymovie.disneyJava.dtos;

import com.disneymovie.disneyJava.models.MovieModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.platform.commons.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharacterModelDto {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer idCharacter;
    private Integer age;
    private Integer weight;
    private String imgUrl;
    private String name;
    private String story;
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<MovieModel> movieModelList = new ArrayList<>();
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Integer> movieIdList = new ArrayList<>();

    @JsonIgnore
    public boolean isValid() {
        return age > 0 &&
                weight > 0 &&
                imgUrl != null &&
                !StringUtils.isBlank(name) &&
                !StringUtils.isBlank(story);
    }
}
